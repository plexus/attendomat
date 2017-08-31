(ns frontend.views
  (:require [frontend.helpers :refer [on-change-handler on-change-dispatch]]
            [attendomat.helpers :refer [present?]]
            [frontend.styles :as styles]
            [garden.core :refer [css]]
            [reagent.core :as r]
            [re-frame.core :as re-frame :refer [dispatch subscribe]]
            [clojure.string :as str]
            [goog.string :as gstring]))

(defn filter-checkbox [state]
  (let [state-filter (subscribe [:state-filter state])]
    (fn []
      (let [{:keys [caption checked count]} @state-filter
            state-str (name state)
            id (str "filter-" state)]
        [:label {:for id
                 :class (str "state-filter-checkbox state-" state-str)
                 :key state-str}
         [:input {:type "checkbox"
                  :name id
                  :id id
                  :checked checked
                  :on-change #(if (.. % -target -checked)
                                (dispatch [:show-state state])
                                (dispatch [:hide-state state]))}]
         caption " (" count ")"]))))

(defn filter-checkboxes []
  [:div#filter-checkboxes
   [filter-checkbox :waiting]
   [filter-checkbox :invited]
   [filter-checkbox :accepted]
   [filter-checkbox :cancelled]])

(defn filter-box []
  (let [filter-value (subscribe [:filter-value])]
    (fn []
      [:input.text {:type "text"
                    :value @filter-value
                    :on-change (on-change-dispatch :set-filter-value)}])))

(defn attendee-list []
  (let [atts (subscribe [:attendees])]
    (fn []
      [:div#attendee-list
       (for [a @atts]
         (let [name-str (str (:first-name a) " " (:last-name a))]
           [:div.entry {:key (:email a)
                        :class (str "state-" (name (:state a)))
                        :on-click #(dispatch [:select-attendee (:email a)])}
            name-str]))])))

(defn action-buttons []
  [:div.action-buttons.buttons
   [:button {:on-click #(dispatch [:fetch-attendees])} "Refresh"]
   [:button {:on-click #(dispatch [:transition-state :invite-more])} "Invite"]
   [:button {:on-click #(dispatch [:summarize])} "Summarize"]])

(defn attendee-list-panel []
  [:div#attendee-list-panel
   [:div.top-bar.top-bar--right
    [:a.button {:on-click #(dispatch [:transition-state :inspector])} "{}"]]
   [filter-checkboxes]
   [filter-box]
   [action-buttons]
   [attendee-list]])

(defn back-button
  ([]
   (back-button :attendee-list))
  ([target]
   [:a.back-arrow.button {:on-click #(dispatch [:transition-state target])} "←"]))

(defn invite-more-panel []
  (let [invite-count (r/atom "")]
    (fn []
      [:div#invite-more-panel
       [:div.top-bar [back-button]]
       [:p "How many people do you want to invite?"]
       [:input.text {:type "text"
                     :value @invite-count
                     :on-change (on-change-handler invite-count)}]
       [:button {:on-click (fn [_]
                             (dispatch [:invite-more (js/parseInt @invite-count)])
                             (dispatch [:transition-state :attendee-list]))}
        "Invite"]])))

(defn attendee-state-change-buttons [state email]
  [:div.action-buttons.buttons
   (if-not (= state :invited)
     [:button.state-invited
      {:on-click #(dispatch [:add-event "INVITED" email])}
      "Invite"])
   (if-not (= state :accepted)
     [:button.state-accepted
      {:on-click #(dispatch [:add-event "ACCEPTED" email])}
      "Accept"])
   (if-not (= state :cancelled)
     [:button.state-cancelled
      {:on-click #(dispatch [:add-event "CANCELLED" email])}
      "Cancel"])
   [:button
    {:on-click #(dispatch [:show-comment-form])}
    "Comment"]])


(defn comment-form [email]
  (let [comment (r/atom "")]
    (fn []
      [:div
       [:textarea {:on-change (on-change-handler comment)}]
       [:button {:on-click #(do (dispatch [:add-comment email @comment])
                                (dispatch [:hide-comment-form]))} "Add comment"]])))

(defn selected-attendee-panel []
  (let [attendee (subscribe [:selected-attendee])
        show-comment-form (subscribe [:show-comment-form])]
    (fn []
      (let [{:keys [first-name last-name email state age gender
                    experience-other experience-clojure language-prefs
                    food-prefs assistance childcare heard-of-us comment
                    history travel]} @attendee]
        [:div#selected-attendee
         [:div.top-bar
          [back-button]
          [:div.button.button--mail {:on-click #(dispatch [:goto-emails email])} "🖂"]]
         [:div.flex
          [:div.user-name  first-name " " last-name]
          [:div.label {:class (str "state-" (name state))} (name state)]]
         [:p email]
         (if @show-comment-form
           [comment-form email]
           [attendee-state-change-buttons state email])
         [:div
          [:table {:width "100%"}
           [:tbody
            (for [{:keys [timestamp state type args]} history]
              (let [note (second args)
                    state (or state :comment)]
                (list
                 [:tr {:key (.toString timestamp)
                       :class (str "state-" (name state))}
                  [:td (and timestamp (.toDateString timestamp))]
                  [:td type]]
                 (if (present? note)
                   [:tr {:key (str (.toString timestamp) "-note")
                         :class (str "state-" (name state))}
                    [:td {:col-span "2"} note]]))))]]]
         (when (not= age "18 and over | 18 und älter")
           [:p "⚠" age])
         [:p gender]
         (if (present? experience-other)
           [:div
            [:h3 "🚧 Experience"]
            [:p experience-other]])
         (if (present? experience-clojure)
           [:div
            [:h3 "🖳 Experience in Clojure"]
            [:p experience-clojure]])
         (if (present? heard-of-us)
           [:div
            [:h3 "👂 How did you hear of us"]
            [:p heard-of-us]])
         (if (present? comment)
           [:div
            [:h3 "💭 Comment"]
            [:p comment]])
         (if (present? language-prefs)
           [:div
            [:h3 "💬 Language preference"]
            [:p language-prefs]])
         (if (present? food-prefs)
           [:div
            [:h3 "🍴 Food preference"]
            [:p food-prefs]])
         (if (present? assistance)
           [:div
            [:h3 "♿ Assistance"]
            [:p assistance]])
         (if (present? childcare)
           [:div
            [:h3 "👶 Childcare"]
            [:p childcare]])
         (if (present? travel)
           [:div
            [:h3 "🛬 Travel from outside Berlin"]
            [:p travel]])]))))

(defn inspector-panel []
  (let [attendees (subscribe [:attendees])]
    (fn []
      [:div#inspector
       [:div.top-bar
        [back-button]]
       [:textarea {:rows "35" :value (prn-str @attendees)}]])))

(def month-names ["Jan" "Feb" "Mar" "Apr" "May" "Jun" "Jul" "Aug" "Sep" "Oct" "Nov" "Dec"])

(defn email-entry [{:keys [id subject body date]}]
  [:div.email-entry
   [:div.subject-row
    [:div.subject [:a {:href (str "https://mail.google.com/mail/u/0/#inbox/" id) :target "_blank"} subject]]
    [:div.date (.getDate date) " " (get month-names (.getMonth date)) " " (.getHours date) ":" (gstring/format "%02f" (.getMinutes date)) ]]
   [:div.preview (subs body 0 150) "..."]])

(defn emails-panel []
  (let [attendee (subscribe [:selected-attendee])
        emails (subscribe [:selected-attendee-emails])]
    (fn []
      (let [{:keys [email]} @attendee
            emails @emails]
        [:div#inspector
         [:div.top-bar
          [back-button :selected-attendee]]
         [:p "from:" email]
         (for [message emails]
           ^{:key (:id message)} [email-entry message])]))))

(defn main-panel []
  (let [state (subscribe [:state])]
    (fn []
      [:div
       [:style {:type "text/css"} (css styles/styles)]
       (case @state
         :attendee-list      [attendee-list-panel]
         :invite-more        [invite-more-panel]
         :selected-attendee  [selected-attendee-panel]
         :inspector          [inspector-panel]
         :emails             [emails-panel]
         [:div (prn-str @state)])])))
