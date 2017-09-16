(ns frontend.events
  (:require [re-frame.core :as re-frame :refer [reg-event-db reg-event-fx reg-fx dispatch debug]]
            [frontend.db :as db]
            [frontend.backend-calls :refer [server-call]]
            [attendomat.attendees :as attendees]
            [clojure.set :as set]))

(defn backend-call [effects call-args]
  (let [uuid (random-uuid)
        call-args (assoc call-args :uuid uuid)]
    (-> effects
        (assoc-in [:db :backend/active-calls uuid] call-args)
        (assoc :backend call-args))))

(reg-fx :backend (fn [{call :call disp :dispatch uuid :uuid}]
                   (let [disp (cond-> disp (keyword? disp) vector)]
                     (server-call call
                                  (fn [res]
                                    (dispatch [:backend/done uuid])
                                    (dispatch (conj disp res)))))))

(reg-event-db :initialize-db
              (fn  [_ _]
                db/default-db))

(reg-event-db :backend/done
              (fn [db [_ uuid]]
                (update db :backend/active-calls dissoc uuid)))

(reg-event-fx :fetch-attendees
              (fn [{:keys [db]} _]
                (backend-call {:db (assoc db :menu-open? false)}
                              {:call [:attendee-data]
                               :caption "Reloading attendee data from spreadsheet"
                               :dispatch :reset-attendees})))

(reg-event-fx :add-event
              (fn [{:keys [db]} [_ type & args]]
                (backend-call {:db db}
                              {:call [:add-event type args]
                               :caption (str "Adding event " type " " args)
                               :dispatch :reset-attendees})))

(reg-event-fx :invite-more
              (fn [{:keys [db]} [_ count]]
                (let [selection (attendees/randomly-select (:attendees db) :waiting count)]
                  (backend-call {:db db} {:call [:create-invite-batch selection]
                                          :caption (str "Inviting batch of " count)
                                          :dispatch :reset-attendees}))))

(reg-event-fx :select-attendee
              (fn [{:keys [db]} [_ at]]
                {:db (assoc db :selected-attendee at)
                 :dispatch [:transition-state :selected-attendee]}))

(reg-event-db :reset-attendees
              (fn [db [_ atts]]
                (assoc db :attendees atts)))

(reg-event-db :transition-state
              (fn [db [_ new-state]]
                (assoc db
                       :previous-state (:state db)
                       :state new-state
                       :menu-open? false)))

(reg-event-db :set-filter-value
              (fn [db [_ new-value]]
                (assoc db :filter-value new-value)))

(reg-event-db :show-state
              (fn [db [_ state]]
                (update-in db [:show-states] conj state)))

(reg-event-db :hide-state
              (fn [db [_ state]]
                (update-in db [:show-states] #(set/difference % #{state}))))

(reg-event-db :show-comment-form
              (fn [db]
                (assoc db :show-comment-form true)))

(reg-event-db :hide-comment-form
              (fn [db]
                (assoc db :show-comment-form false)))

(reg-event-fx :add-comment
              (fn [_ [_ email comment]]
                {:dispatch [:add-event "COMMENT" email comment]}))

(reg-event-fx :summarize
              (fn [{:keys [db]} _]
                (-> {:db (assoc db :menu-open? false)}
                    (backend-call {:call [:summarize]
                                   :caption "Generating result sheets"}))))

(reg-event-fx :goto-emails
              (fn [{:keys [db]} [_ email]]
                (-> {:db db
                     :dispatch [:transition-state :emails]}
                    (backend-call {:call [:fetch-emails-from email]
                                   :caption (str "Finding emails recieved from " email)
                                   :dispatch :merge-emails}))))

(reg-event-db :merge-emails
              (fn [db [_ emails]]
                (update db :emails merge emails)))

(reg-event-db :toggle-menu
              (fn [db _]
                (update db :menu-open? not)))
