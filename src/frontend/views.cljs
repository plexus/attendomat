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
        [:div {:on-click #(dispatch [:toggle-menu])} title]
        [:div.flex
         (when (not (empty? @active-calls))
           [:img {:src "https://arnebrasseur.net/hourglass-dark.gif"
                  :title (str/join "\n" (map :caption (vals @active-calls)))
                  :height "34px"
                  :width "34px"}])
         [:div.pointer
          [:a {:on-click #(dispatch [:toggle-menu])} (if @menu-open? "︽" "☰")]]]]))))

(defn coaches-list-panel []
  (let [coaches-spreadsheet (subscribe [:coaches-spreadsheet])
        coaches (subscribe [:coaches])
        url-input (r/atom "")]
    (fn []
      [:div#coaches-list
       [menu-bar "Coaches"]
       (if (nil? @coaches-spreadsheet)
         [:div.mh2
          [:p "The coaches response sheet isn't configured yet."]
          [:p "Copy in the URL to the Google Sheet containing the Coaches Sign Up Responses."]
          [:input.text {:type "text"
                        :value @url-input
                        :placeholder "URL to sheet"
                        :on-change (on-change-handler url-input)}]
          [:button {:on-click #(dispatch [:add-event "SET_COACHES_SPREADSHEET" @url-input])} "Update"]]
         [:div.cb
          (let [coaches @coaches]
            (if (empty? coaches)
              [:p "No coaches have signed up yet."]
              (for [c coaches]
                (let [name-str (str (:first-name c) " " (:last-name c))]
                  [:div.entry {:key (:email c)
                               :class (str "state-" (name (:state c)))
                               :on-click #(dispatch [:select-coach (:email c)])}
                   name-str]))))])])))

(defn attendee-list-panel []
  [:div#attendee-list-panel
   [menu-bar "Attendees"]
   [:div.cb
    [filter-box]
    [filter-checkboxes]
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
   (when (= state :waiting)
     [:button.state-invited
      {:on-click #(dispatch [:add-event "INVITED" email])}
      "Invite"])
   (when (contains? #{:waiting :invited :cancelled} state)
     [:button.state-accepted
      {:on-click #(dispatch [:add-event "ACCEPTED" email])}
      "Accept"])
   (when (contains? #{:waiting :invited :accepted} state)
     [:button.state-cancelled
      {:on-click #(dispatch [:add-event "CANCELLED" email])}
      "Cancel"])])

(defn coach-state-change-buttons [state email]
  [:div.action-buttons.buttons
   (when (contains? #{:waiting :cancelled} state)
     [:button.state-accepted
      {:on-click #(dispatch [:add-event "ACCEPTED_COACH" email])}
      "Accept"])
   (when (contains? #{:waiting :accepted} state)
     [:button.state-cancelled
      {:on-click #(dispatch [:add-event "REJECTED_COACH" email])}
      "Reject"])])

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

(defn selected-coach-panel []
  (let [coach (subscribe [:selected-coach])]
    (fn []
      (let [{:keys [first-name last-name email food-prefs phone experience-coaching
                    history state travel childcare coaching-prefs assistance
                    gender tshirt-size timestamp floating-coach? other-langs
                    experience-clojure language-prefs comment]} @coach]
        [:div#selected-coach
         [menu-bar "Coach" :coaches-list]
         [:div.tc.pv2 {:class (str "state-" (name state))}
          [:div.f3 str first-name " " last-name]
          [:div.small-caps (name state)]]

         [:div.mt2.pb2
          [coach-state-change-buttons state email]]

         [:div.pt2 "🖁 " phone]
         [:div.pt2 "🖂 " email]
         [:div.ph2.pb2.w-100.tr
          [:a.blue.bb.b--blue.pointer {:on-click #(dispatch [:goto-emails email])} "View emails"]]

         [:h3 "Gender"]
         [:p gender]
         (if (present? experience-coaching)
           [:div
            [:h3 "🚧 Experience Coaching"]
            [:p experience-coaching]])
         (if (present? experience-clojure)
           [:div
            [:h3 "Experience in Clojure"]
            [:p experience-clojure]])
         (if (present? language-prefs)
           [:div
            [:h3 "💬 Language preference"]
            [:p language-prefs]])
         (if (present? coaching-prefs)
           [:div
            [:h3 "Coaching preference"]
            [:p coaching-prefs]])
         (if (present? floating-coach?)
           [:div
            [:h3 "Ok with floating coach?"]
            [:p floating-coach?]])
         (if (present? food-prefs)
           [:div
            [:h3 "🍴 Food preference"]
            [:p food-prefs]])
         (if (present? comment)
           [:div
            [:h3 "💭 Comment"]
            [:p comment]])
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
           :selected-coach     [selected-coach-panel]
           :inspector          [inspector-panel]
           :emails             [emails-panel]
           :working            [:div
                                [menu-bar "Working..."]
                                "Working..."]
           :done               [:div
                                [menu-bar "Done!"]
                                "Done!"]
           [:div "Unkown state:" (prn-str @state)]))])))
