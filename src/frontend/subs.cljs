(ns frontend.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame :refer [reg-sub]]
            [clojure.string :as str]))

(defn str-include? [haystack needle]
  (.includes (str/lower-case haystack)
             (str/lower-case needle)))

(reg-sub :state (fn [db] (:state db)))
(reg-sub :filter-value (fn [db] (:filter-value db)))
(reg-sub :show-states (fn [db] (:show-states db)))
(reg-sub :selected-attendee
         (fn [db]
           (first
            (filter
             #(= (:email %) (:selected-attendee db))
             (:attendees db))) ))

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
