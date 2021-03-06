(ns frontend.events
  (:require [re-frame.core :as re-frame :refer [reg-event-db reg-event-fx reg-fx dispatch debug]]
            [frontend.db :as db]
            [frontend.backend-calls :refer [server-call]]
            [attendomat.attendees :as attendees]
            [clojure.set :as set]
            [backend.event-sourcing :refer [apply-events]]))

(defn backend-call [effects call-args]
  (let [uuid (random-uuid)
        call-args (assoc call-args :uuid uuid)]
    (-> effects
        (assoc-in [:db :backend/active-calls uuid] call-args)
        (assoc :backend call-args))))

(defn add-event [db type args]
  (backend-call {:db (apply-events db [{:timestamp (js/Date.) :type type :args args}])}
                {:call [:add-event type args]
                 :caption (str "Adding event " type " " args)
                 :dispatch :merge-app-data}))

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

(reg-event-fx :fetch-app-data
              (fn [{:keys [db]} _]
                (backend-call {:db (assoc db :menu-open? false)}
                              {:call [:app-data]
                               :caption "Reloading data from spreadsheet"
                               :dispatch :merge-app-data})))

(reg-event-fx :add-event
              (fn [{:keys [db]} [_ type & args]]
                (add-event db type args)))

(reg-event-fx :invite-more
              (fn [{:keys [db]} [_ count]]
                (let [selection (attendees/randomly-select (vals (:attendees db)) :waiting count)]
                  (backend-call {:db db} {:call [:create-invite-batch selection]
                                          :caption (str "Inviting batch of " count)
                                          :dispatch :merge-app-data}))))

(reg-event-fx :select-attendee
              (fn [{:keys [db]} [_ at]]
                {:db (assoc db :selected-attendee at)
                 :dispatch [:transition-state :selected-attendee]}))

(reg-event-fx :select-coach
              (fn [{:keys [db]} [_ at]]
                {:db (assoc db :selected-coach at)
                 :dispatch [:transition-state :selected-coach]}))

(reg-event-db :merge-app-data
              (fn [db [_ data]]
                (merge db data)))

(reg-event-db :transition-state
              (fn [db [_ new-state]]
                (js/window.scrollTo 0 0) ;; hax00r
                (assoc db
                       :previous-state (:state db)
                       :state new-state
                       :menu-open? false)))

(reg-event-db :transition-inspector-state
              (fn [db [_ new-state]]
                (assoc db :inspector-state new-state)))

(reg-event-db :set-filter-value
              (fn [db [_ new-value]]
                (assoc db :filter-value new-value)))

(reg-event-db :set-filter-coach
              (fn [db [_ new-value]]
                (assoc db :filter-coach new-value)))

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
              (fn [{:keys [db]} [_ email comment]]
                (add-event db "COMMENT" [email comment])))

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

(def group-names-1 ["Dashing" "Amazing" "Wonderful" "Shimmering" "Intrepid" "Radical"
                    "Generous" "Creative" "Tranquil" "Sparkling" "Cool" "Indelible"
                    "Active" "Terrific" "Poetic" "Inspiring" "Tiny"])

(def group-names-2 ["Unicorns" "Rainbows" "Stars" "Sparkles" "Elements" "Squirrels"
                    "Cats" "Ponies" "Travelers" "Programmers" "Functions" "Atoms" "Artists"
                    "Volcanoes" "Springs" "Suns" "Monsters" "Bees" "Droplets"
                    "Koalas" "Pandas"])

(defn rand-group-name []
  (str (rand-nth group-names-1) " " (rand-nth group-names-2)))

(reg-event-fx :add-group
              (fn [{{:keys [groups] :as db} :db} _]
                (let [name (loop [name (rand-group-name)]
                             (if (some #{name} (map :name (vals groups)))
                               (recur (rand-group-name))
                               name))]
                  (add-event db "CREATE_GROUP" [(str (random-uuid)) name])) ))

(reg-event-db :show-group-overlay-menu
              (fn [db [_ email on-move x y]]
                (assoc db :group-overlay-menu [email on-move x y])))

(reg-event-db :hide-group-overlay-menu
              (fn [db _]
                (dissoc db :group-overlay-menu)))


(reg-event-fx :assign-attendee
              (fn [{:keys [db]} [_ group-id email]]
                (add-event db "ASSIGN_ATTENDEE" [group-id email])))


(reg-event-fx :assign-coach
              (fn [{:keys [db]} [_ group-id email]]
                (-> db
                    (dissoc :group-overlay-menu)
                    (add-event "ASSIGN_COACH" [group-id email]))))
