(ns frontend.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame :refer [reg-sub]]
            [clojure.string :as str]))

(defn str-include? [haystack needle]
  (.includes (str/lower-case (str haystack))
             (str/lower-case (str needle))))

(reg-sub :state (fn [db] (:state db)))
(reg-sub :inspector-state (fn [db] (:inspector-state db)))
(reg-sub :attendees (fn [db] (vals (:attendees db))))
(reg-sub :coaches (fn [db] (vals (:coaches db))))
(reg-sub :coaches-spreadsheet (fn [db] (:coaches-spreadsheet db)))
(reg-sub :filter-value (fn [db] (:filter-value db)))
(reg-sub :filter-coach (fn [db] (:filter-coach db)))
(reg-sub :show-states (fn [db] (:show-states db)))
(reg-sub :show-comment-form (fn [db] (:show-comment-form db)))
(reg-sub :emails (fn [db] (:emails db)))
(reg-sub :menu-open? (fn [db] (:menu-open? db)))
(reg-sub :previous-state (fn [db] (:previous-state db)))
(reg-sub :backend/active-calls (fn [db] (:backend/active-calls db)))
(reg-sub :group-overlay-menu (fn [db] (:group-overlay-menu db)))

(reg-sub :selected-attendee
         (fn [db]
           (first
            (filter
             #(= (:email %) (:selected-attendee db))
             (vals (:attendees db)))) ))

(reg-sub :selected-coach
         (fn [db]
           (first
            (filter
             #(= (:email %) (:selected-coach db))
             (vals (:coaches db)))) ))

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
             (vals attendees)))))


(reg-sub
 :filtered-coaches
 (fn [db]
   (let [{:keys [filter-coach coaches]} db]
     (filter (fn [a]
               (or (empty? filter-coach)
                   (some #(str-include? (% a) filter-coach)
                         [:first-name :last-name :email])))
             (vals coaches)))))


;; Attendees shown in the attendee list
(reg-sub
 :visible-attendees
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

(reg-sub :groups
         (fn [{:keys [groups]} _]
           (vals groups)))

(defn unassigned [type db _]
  (let [people (vals (get db type))]
    (->> people
         (remove :group)
         (filter #(= (:state %) :accepted)))))

(reg-sub :unassigned-attendees (partial unassigned :attendees))
(reg-sub :unassigned-coaches (partial unassigned :coaches))
