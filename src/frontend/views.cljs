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
        [:div.pv1 {:class (str "state-filter-checkbox state-" state-str)}
         [:input.mh1 {:type "checkbox"
                      :name id
                      :id id
                      :checked checked
                      :on-change #(if (.. % -target -checked)
                                    (dispatch [:show-state state])
                                    (dispatch [:hide-state state]))}]
         [:label {:for id :key state-str} caption " (" count ")"]]))))

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
                    :placeholder "🔎 Filter"
                    :value @filter-value
                    :on-change (on-change-dispatch :set-filter-value)}])))

(defn attendee-list []
  (let [atts (subscribe [:visible-attendees])]
    (fn []
      [:div#attendee-list
       (for [a @atts]
         (let [name-str (str (:first-name a) " " (:last-name a))]
           [:div.entry {:key (:email a)
                        :class (str "state-" (name (:state a)))
                        :on-click #(dispatch [:select-attendee (:email a)])}
            name-str]))])))

(defn menu-bar
  ([title]
   [menu-bar title nil])
  ([title back]
   (let [menu-open? (subscribe [:menu-open?])
         active-calls (subscribe [:backend/active-calls])]
     (fn []
       [:div#menu-bar.flex.f3.bg-black.near-white.ph1.pv1.mb1 {:style {:line-height "34px"}}
        (when back
          [:div.br-100.dark-gray.bg-near-white.pointer.tc {:style {:width "34px" :height "34px"}}
           [:a {:on-click #(dispatch [:transition-state back])} "←"]])
        [:div title]
        [:div.flex
         (when (not (empty? @active-calls))
           [:img {:src "https://arnebrasseur.net/hourglass-dark.gif"
                  :title (str/join "\n" (map :caption (vals @active-calls)))
                  :height "34px"
                  :width "34px"}])
         [:div.pointer
          [:a {:on-click #(dispatch [:toggle-menu])} (if @menu-open? "︽" "☰")]]]]))))

(defn action-buttons []
  [:div.action-buttons.buttons
   [:button {:on-click #(dispatch [:transition-state :invite-more])} "Invite"]
   [:button {:on-click #(dispatch [:summarize])} "Summarize"]])

(defn coaches-list-panel []
  (let [coaches (subscribe [:coaches])]
    [:div#coaches-list-panel
     [menu-bar "Coaches"]
     [:div.cb
      (for [c @coaches]
        (let [name-str (str (:first-name c) " " (:last-name c))]
          [:div.entry {:key (:email c)} name-str]))]]))

(defn attendee-list-panel []
  [:div#attendee-list-panel
   [menu-bar "Attendees"]
   [:div.cb
    [filter-box]
    [filter-checkboxes]
    #_[action-buttons]
    [attendee-list]]])

(defn back-button
  ([]
   (back-button :attendee-list))
  ([target]
   [:a.back-arrow.button {:on-click #(dispatch [:transition-state target])} "←"]))

(defn invite-more-panel []
  (let [invite-count (r/atom "")
        previous-state (subscribe [:previous-state])]
    (fn []
      [:div#invite-more-panel
       [menu-bar "Invite more" @previous-state]
       [:div.mh2.tc
        [:p "How many people do you want to invite?"]
        [:input.tr.text {:type "text"
                         :value @invite-count
                         :placeholder "0"
                         :on-change (on-change-handler invite-count)}]
        [:button {:on-click (fn [_]
                              (dispatch [:invite-more (js/parseInt @invite-count)])
                              (dispatch [:transition-state :attendee-list]))}
         "Invite"]]])))

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
      "Cancel"])])


(defn comment-form [email]
  (let [comment (r/atom "")]
    (fn []
      [:div.tc
       [:div.mh1
        [:textarea {:on-change (on-change-handler comment)}]]
       [:button.ma2 {:on-click #(do (dispatch [:add-comment email @comment])
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
         [menu-bar "Attendee" :attendee-list]
         [:div.tc.pv2 {:class (str "state-" (name state))}
          [:div.f3 str first-name " " last-name]
          [:div.small-caps (name state)]]
         [:div.mt2.pb2
          [attendee-state-change-buttons state email]]
         (when (not (empty? history))
           [:div.pa1
            [:div.f4 "History"]
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
                      [:td {:col-span "2"} note]]))))]]])

         (if @show-comment-form
           [comment-form email]
           [:div.ph2.pb2.w-100.tr
            [:a.blue.bb.b--blue.pointer {:on-click #(dispatch [:show-comment-form])} "Add comment"]])

         [:div.pt2 "🖂 " email]
         [:div.ph2.pb2.w-100.tr
          [:a.blue.bb.b--blue.pointer {:on-click #(dispatch [:goto-emails email])} "View emails"]]

         (when (not= age "18 and over | 18 und älter")
           [:div
            [:h3 "Age"]
            [:p "⚠" age]])
         [:h3 "Gender"]
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
  (let [attendees (subscribe [:attendees])
        coaches (subscribe [:coaches])
        inspector-state (subscribe [:inspector-state])]
    (fn []
      [:div#inspector
       [menu-bar "Export EDN" :attendee-list]
       [:ul.list.ph1.ma0
        [:li.pb2 [:a.blue.bb.b--blue.pointer {:on-click #(dispatch [:transition-inspector-state :attendees])} "Attendees"]]
        [:li [:a.blue.bb.b--blue.pointer {:on-click #(dispatch [:transition-inspector-state :coaches])} "Coaches"]]]
       [:h3.ph1 (case @inspector-state
                  :attendees "attendees.edn"
                  :coaches "coaches.edn"
                  nil "")]
       [:textarea {:rows "35" :value (case @inspector-state
                                       :attendees (prn-str @attendees)
                                       :coaches (prn-str @coaches)
                                       nil "")}]])))

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
        [:div
         [menu-bar "Emails" :selected-attendee]
         [:p.bb.b--black "From: " email]
         (for [message emails]
           ^{:key (:id message)} [email-entry message])]))))


(defn menu-panel []
  [:div#menu-panel
   [menu-bar "Menu"]
   [:ul.list
    [:li [:a {:on-click #(dispatch [:transition-state :attendee-list])} "Attendee List"]]
    [:li [:a {:on-click #(dispatch [:transition-state :coaches-list])} "Coaches List"]]
    [:li [:a {:on-click #(dispatch [:transition-state :invite-more])} "Invite More"]]
    [:li [:a {:on-click #(dispatch [:summarize])} "Generate Result Sheets"]]
    [:li [:a {:on-click #(dispatch [:fetch-app-data])} "Reload Spreadsheet Data"]]
    [:li [:a {:on-click #(dispatch [:transition-state :inspector])} "Export EDN"]]]])

(defn main-panel []
  (let [state (subscribe [:state])
        menu-open? (subscribe [:menu-open?])]
    (fn []
      [:div
       [:style {:type "text/css"} (css styles/styles)]
       (if @menu-open?
         [menu-panel]
         (case @state
           :attendee-list      [attendee-list-panel]
           :coaches-list       [coaches-list-panel]
           :invite-more        [invite-more-panel]
           :selected-attendee  [selected-attendee-panel]
           :inspector          [inspector-panel]
           :emails             [emails-panel]
           :working            [:div
                                [menu-bar "Working..."]
                                "Working..."]
           :done               [:div
                                [menu-bar "Done!"]
                                "Done!"]
           [:div "Unkown state:" (prn-str @state)]))])))
