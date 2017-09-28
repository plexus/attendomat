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

(defn apply-event [data {:keys [type args] :as event}]
  (cond
    (contains? #{"COMMENT" "WAITING" "INVITED" "ACCEPTED" "CANCELLED"} type)
    (update data :attendees apply-person-event event)

    (contains? #{"ACCEPTED_COACH" "REJECTED_COACH"} type)
    (update data :coaches apply-person-event event)

    (= "SET_COACHES_SPREADSHEET" type)
    (let [[url] args]
      (assoc data
             :coaches-spreadsheet url
             :coaches (map-by-email (coaches/read-spreadsheet-data url))))

    :else
    data))

(defn app-data []
  (let [events (event-data)
        attendees (map-by-email (attendee-data-raw))
        data {:attendees attendees
              :coaches {}
              :coaches-spreadsheet nil}]
    (-> (reduce apply-event data events)
        (update :attendees vals)
        (update :coaches vals))))


;; (let [attendees (attendees-by-email [{:email "x@y.be"
;;                                         :state :waiting}
;;                                        {:email "f@g.be"
;;                                         :state :missing}])
;;         events [{:type "INVITED" :args ["x@y.be"]}
;;                 {:type "INVITED" :args ["f@g.be"]}]]
;;     (vals (reduce (fn [ats ev]
;;                     (apply-event ats ev)) attendees events)))
