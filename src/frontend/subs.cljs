(ns frontend.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub :attendees (fn [db]
                               (let [{:keys [filter-value attendees show-states]} db]
                                 (filter (fn [a]
                                           (and
                                            (or (empty? filter-value)
                                                (some #(.includes (% a) filter-value) [:first-name :last-name :email]))
                                            (show-states (:state a))))
                                         attendees))))

(re-frame/reg-sub :state (fn [db]
                           (:state db)))

(re-frame/reg-sub :filter-value (fn [db]
                                  (:filter-value db)))

(re-frame/reg-sub :show-states (fn [db]
                                 (:show-states db)))

(re-frame/reg-sub :selected-attendee (fn [db]
                                       (:selected-attendee db)))
