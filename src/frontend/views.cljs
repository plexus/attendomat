(ns frontend.views
  (:require [frontend.helpers :refer [on-change-handler on-change-dispatch present?]]
            [frontend.styles :as styles]
            [garden.core :refer [css]]
            [reagent.core :as r]
            [re-frame.core :as re-frame :refer [dispatch subscribe]]))

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
                        :on-click #(dispatch [:select-attendee a])}
            name-str]))])))

(defn action-buttons []
  [:div#action-buttons.buttons
   [:button {:on-click #(dispatch [:fetch-attendees])} "Refresh"]
   [:button {:on-click #(dispatch [:transition-state :invite-more])} "Invite"]])

(defn attendee-list-panel []
  [:div#attendee-list-panel
   [filter-checkboxes]
   [filter-box]
   [action-buttons]
   [attendee-list]])

(defn invite-more-panel []
  (let [invite-count (r/atom "")]
    (fn []
      [:div#invite-more-panel
       [:p [:a {:on-click #(dispatch [:transition-state :attendee-list])} "<--"]]
       [:p "How many people do you want to invite?"]
       [:input.text {:type "text"
                     :value @invite-count
                     :on-change (on-change-handler invite-count)}]
       [:button {:on-click (fn [_]
                             (dispatch [:invite-more (js/parseInt @invite-count)])
                             (dispatch [:transition-state :attendee-list]))}
        "Invite"]])))

(defn selected-attendee-panel []
  (let [attendee (subscribe [:selected-attendee])]
    (fn []
      (let [{:keys [first-name
                    last-name
                    email
                    state
                    age
                    gender
                    experience-other
                    experience-clojure
                    language-prefs
                    food-prefs
                    assistance
                    childcare
                    heard-of-us
                    comment]} @attendee]
        [:div#selected-attendee
         [:p [:a {:on-click #(dispatch [:transition-state :attendee-list])} "<--"]]
         [:p {:class (str "state-" (name state))} first-name " " last-name " (" (name state) ")"]
         [:p email]
         [:p age]
         [:p gender]
         (if (present? experience-other)
           [:div
            [:h3 "Experience"]
            [:p experience-other]])
         (if (present? experience-clojure)
           [:div
            [:h3 "Experience in Clojure"]
            [:p experience-clojure]])
         ;; (if (present? language-prefs)
         ;;   [:div
         ;;    [:h3 "language-prefs"]
         ;;    [:p language-prefs]])
         (if (present? food-prefs)
           [:div
            [:h3 "Food preference"]
            [:p food-prefs]])
         (if (present? assistance)
           [:div
            [:h3 "Assistance"]
            [:p assistance]])
         (if (present? childcare)
           [:div
            [:h3 "Childcare"]
            [:p childcare]])
         (if (present? heard-of-us)
           [:div
            [:h3 "How did you hear of us"]
            [:p heard-of-us]])
         (if (present? comment)
           [:div
            [:h3 "Comment"]
            [:p comment]])
         [:div.buttons
          (if-not (= state :invited)
            [:button.state-invited "Invite"])
          (if-not (= state :accepted)
            [:button.state-accepted {
              :on-click #(dispatch [:add-event "ACCEPTED" email])
            } "Accept"])
          (if-not (= state :cancelled)
            [:button.state-cancelled "Cancel"])
          ]]))))

(defn main-panel []
  (let [state (subscribe [:state])]
    (fn []
      [:div
       [:style {:type "text/css"} (css styles/styles)]
       (case @state
         :attendee-list [attendee-list-panel]
         :invite-more   [invite-more-panel]
         :selected-attendee   [selected-attendee-panel]
         [:div (prn-str @state)])])))
