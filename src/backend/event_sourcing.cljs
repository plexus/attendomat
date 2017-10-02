(ns backend.event-sourcing
  (:require [backend.sheets :as sh]
            [attendomat.attendees :as attendees]
            [backend.coaches :as coaches]))

(def event->state {"WAITING" :waiting
                   "INVITED" :invited
                   "ACCEPTED" :accepted
                   "ACCEPTED_COACH" :accepted
                   "CANCELLED" :cancelled
                   "REJECTED_COACH" :rejected})

(defn attendee-data-raw []
  (-> (sh/find-sheet "Form Responses 1")
      sh/data-range
      attendees/parse-attendee-data))

(defn map-by-email [ats]
  (into {} (map (juxt :email identity) ats)))

(defn event-sheet []
  (sh/find-or-create-sheet "*EVENTS*"))

(defn event-data []
  (->> (event-sheet)
       sh/data-range
       (map (fn [[ts type & args]]
              {:timestamp ts
               :type type
               :args args}))))

(defn add-event [type args]
  (sh/append-row (event-sheet)
                 (into [(js/Date.) type] args)))

(defn attendee-state [attendees email]
  (get-in attendees [email :state]))

(defn update-state [email->person email new-state event]
  (assoc-in email->person [email :state] new-state))

(defn append-history [attendees email event]
  (update-in attendees [email :history] conj event))

(defn apply-person-event [attendees {:keys [type args] :as event}]
  (let [email (first args)
        new-state (event->state type)]
    (if new-state
      (-> attendees
          (append-history email (assoc event :state new-state))
          (update-state email new-state event))
      (append-history attendees email event))))

(defn apply-groups-event [groups {:keys [type args] :as event}]
  (case type
    "CREATE_GROUP"
    (assoc groups (first args) {:id (first args)
                                :name (second args)
                                :attendees []
                                :coaches []})))

(defn apply-event [data {:keys [type args] :as event}]
  (cond
    (contains? #{"COMMENT" "WAITING" "INVITED" "ACCEPTED" "CANCELLED"} type)
    (update data :attendees apply-person-event event)

    (contains? #{"ACCEPTED_COACH" "REJECTED_COACH"} type)
    (update data :coaches apply-person-event event)

    (contains? #{"CREATE_GROUP"} type)
    (update data :groups apply-groups-event event)

    (= "ASSIGN_ATTENDEE" type)
    (update-in data [:attendees (second args)] assoc :group (first args))

    (= "ASSIGN_COACH" type)
    (update-in data [:coaches (second args)] assoc :group (first args))

    (= "SET_COACHES_SPREADSHEET" type)
    (let [[url] args]
      (assoc data
             :coaches-spreadsheet url
             :coaches (if (exists? js/SpreadsheetApp)
                        (map-by-email (coaches/read-spreadsheet-data url))
                        {})))

    :else
    data))

(defn accepted? [person]
  (= (:state person) :accepted))

(defn apply-events [data events]
  (let [data (reduce apply-event data events)
        group-ids (keys (:groups data))
        attendees (vals (:attendees data))
        coaches (vals (:coaches data))
        group= #(= (:group %1) %2)
        filter-by-group (fn [people group-id]
                          (->> people
                               (filter #(group= % group-id))
                               (filter accepted?)))]
    (reduce (fn [data group-id]
              (-> data
                  (assoc-in [:groups group-id :attendees] (filter-by-group attendees group-id))
                  (assoc-in [:groups group-id :coaches] (filter-by-group coaches group-id))))
            data
            group-ids)))

(defn app-data []
  (let [events (event-data)
        attendees (map-by-email (attendee-data-raw))
        data {:attendees attendees
              :coaches {}
              :groups {}
              :coaches-spreadsheet nil}]
    (apply-events data events)))


;; (let [attendees (attendees-by-email [{:email "x@y.be"
;;                                         :state :waiting}
;;                                        {:email "f@g.be"
;;                                         :state :missing}])
;;         events [{:type "INVITED" :args ["x@y.be"]}
;;                 {:type "INVITED" :args ["f@g.be"]}]]
;;     (vals (reduce (fn [ats ev]
;;                     (apply-event ats ev)) attendees events)))
