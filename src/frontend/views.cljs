(ns frontend.views
  (:require [frontend.helpers :refer [on-change-handler on-change-dispatch]]
            [frontend.styles :as styles]
            [garden.core :refer [css]]
            [reagent.core :as r]
            [re-frame.core :as re-frame :refer [dispatch subscribe]]))

(defn filter-checkboxes []
  [:div#filter-checkboxes
   (map (fn [state]
           (let [id (str "filter-" state)]
             [:label {:for id :class (str "state-" state)}
              [:input {:type "checkbox" :name id }]
              state]))
         ["waiting" "invited" "accepted" "cancelled"])])

(defn filter-box []
  (let [filter-value (subscribe [:filter-value])]
    (fn []

      [:input.text {:type "text"
                    :value @filter-value
                    :on-change (on-change-dispatch :set-filter-value)}])))

(defn attendee-list []
  (let [atts (subscribe [:attendees])]
    [:div#attendee-list
     (for [a @atts]
       (let [name-str (str (:first-name a) " " (:last-name a))]
         [:div.entry {:key (:email a)
                      :class (str "state-" (name (:state a)))} name-str]))]))


(defn action-buttons []
  [:div#action-buttons
   [:button {:on-click #(dispatch [:fetch-attendees])} "Refresh"]
   [:button {:on-click #(dispatch [:transition-state :invite-more])} "Invite"]])

(defn attendee-list-panel []
  [:div#attendee-list-panel
   [filter-checkboxes]
   [filter-box]
   [attendee-list]
   [action-buttons]])

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

(defn main-panel []
  (js/console.log "drawing main panel")
  (let [state (subscribe [:state])]
    (fn []
      [:div
       [:style {:type "text/css"} (css styles/styles)]
       (case @state
         :attendee-list [attendee-list-panel]
         :invite-more   [invite-more-panel]
         [:div (prn-str @state)])])))
