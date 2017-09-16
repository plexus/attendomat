(ns frontend.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame :refer [reg-sub]]
            [clojure.string :as str]))

(defn str-include? [haystack needle]
  (.includes (str/lower-case (str haystack))
             (str/lower-case (str needle))))

(reg-sub :state (fn [db] (:state db)))
(reg-sub :filter-value (fn [db] (:filter-value db)))
(reg-sub :show-states (fn [db] (:show-states db)))
(reg-sub :show-comment-form (fn [db] (:show-comment-form db)))
(reg-sub :emails (fn [db] (:emails db)))
(reg-sub :menu-open? (fn [db] (:menu-open? db)))
(reg-sub :previous-state (fn [db] (:previous-state db)))
(reg-sub :backend/active-calls (fn [db] (:backend/active-calls db)))

(reg-sub :selected-attendee
         (fn [db]
           (first
            (filter
             #(= (:email %) (:selected-attendee db))
             (:attendees db))) ))

(reg-sub :selected-attendee-emails
         :<- [:selected-attendee]
         :<- [:emails]
         (fn [[attendee emails]]
           (get emails (:email attendee))))

;; Filtered using the search box
(reg-sub
 :filtered-attendees
 (fn [db]
   (let [{:keys [filter-value attendees]} db]
     (filter (fn [a]
               (or (empty? filter-value)
                   (some #(str-include? (% a) filter-value)
                         [:first-name :last-name :email])))
             attendees))))

;; Attendees shown in the attendee list
(reg-sub
 :attendees
 :<- [:filtered-attendees]
 :<- [:show-states]
 (fn [[attendees show-states] ats]
   (filter (comp show-states :state) attendees)))

;; All data to render one of the four checkboxes at the top
(reg-sub
 :state-filter
 :<- [:filtered-attendees]
 :<- [:show-states]
 (fn [[attendees show-states] [_ state]]
   {:caption (name state)
    :checked (boolean (show-states state))
    :count (count (filter #(= state (:state %))
                          attendees))}))
