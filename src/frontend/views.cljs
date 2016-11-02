(ns frontend.views
  (:require [frontend.helpers :refer [on-change-handler]]
            [frontend.styles :as styles]
            [garden.core :refer [css]]
            [reagent.core :as r]
            [re-frame.core :as re-frame :refer [dispatch subscribe]]))

(defn search-box []
  [:input {:type "text"}])

(defn attendee-list []
  (let [atts (subscribe [:attendees])]
    [:div#attendee-list
     (for [a @atts]
       (let [name (str (:first-name a) " " (:last-name a) " (" (:state a) ")")]
         [:div {:key name} name]))]))

(defn action-buttons []
  [:div#action-buttons
   [:button {:on-click #(dispatch [:fetch-attendees])} "Refresh"]
   [:button {:on-click #(dispatch [:transition-state :invite-more])} "Invite"]])

(defn attendee-list-panel []
  [:div#attendee-list-panel
   [search-box]
   [attendee-list]
   [action-buttons]])

(defn invite-more-panel []
  (let [invite-count (r/atom "")]
    (fn []
      [:div#invite-more-panel
       [:p [:a {:on-click #(dispatch [:transition-state :attendee-list])} "<--"]]
       [:p "How many people do you want to invite?"]
       [:input {:type "text"
                :value @invite-count
                :on-change (on-change-handler invite-count)}]
       [:button {:on-click (fn [_]
                             (dispatch [:invite-more (js/parseInt @invite-count)])
                             (dispatch [:transition-state :attendee-list]))}
        "Invite"]])))

(defn main-panel []
  (let [state (subscribe [:state])]
    (fn []
      [:div
       [:style {:type "text/css"} (css styles/styles)]
       (case @state
         :attendee-list [attendee-list-panel]
         :invite-more   [invite-more-panel])])))
