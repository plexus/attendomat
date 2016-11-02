(ns frontend.events
    (:require [re-frame.core :as re-frame :refer [reg-event-db reg-event-fx reg-fx dispatch debug]]
              [frontend.db :as db]
              [frontend.backend-calls :refer [server-call]]
              [attendomat.attendees :as attendees]))

(reg-fx :backend (fn [{call :call disp :dispatch}]
                      (server-call call
                                   (fn [res] (dispatch [disp res])))))

(reg-event-db :initialize-db
              (fn  [_ _]
                db/default-db))

(reg-event-fx :fetch-attendees [debug]
              (fn [_ _]
                {:backend {:call [:attendee-data]
                           :dispatch :reset-attendees}}))

(reg-event-fx :add-event [debug]
              (fn [_ [_ type & args]]
                {:backend {:call [:add-event type args]
                           :dispatch :reset-attendees}}))

(reg-event-fx :invite-more [debug]
              (fn [{:keys [db]} [_ count]]
                (let [selection (attendees/randomly-select (:attendees db) :waiting count)]
                  {:backend {:call [:create-invite-batch selection]
                             :dispatch :reset-attendees}})))

(reg-event-db :reset-attendees [debug]
              (fn [db [_ atts]]
                (assoc db :attendees atts)))

(reg-event-db :transition-state [debug]
             (fn [db [_ new-state]]
               (assoc db :state new-state)))
