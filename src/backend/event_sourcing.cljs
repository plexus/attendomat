(ns backend.event-sourcing
  (:require [backend.sheets :as sh]
            [attendomat.attendees :refer [parse-attendee-data]]))

(defn attendee-data-raw []
  (-> (sh/find-sheet "Form Responses 1")
      sh/data-range
      parse-attendee-data))

(defn attendees-by-email [ats]
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
  (get-in attendees [email :state])
)

(defn update-attendee-state [attendees email new-state]
  (assoc-in attendees [email :state] new-state)
)

(defn apply-event [attendees {:keys [timestamp type args]}]
  (case type
    "INVITED" (let [email (first args)
                    old-state (attendee-state attendees email)]
               (if (= old-state :waiting)
                 (update-attendee-state attendees email :invited)
                 (do
                   (sh/error! (str "Tried to invite " email " but current state is already :" (name old-state) " instead of :waiting"))
                   attendees)))
    "ACCEPTED" (let [email (first args)
                     old-state (attendee-state attendees email)]
                   (update-attendee-state attendees email :accepted))
    attendees))


(defn attendee-data []
  (let [events (event-data)
        attendees (attendees-by-email (attendee-data-raw))]
    (vals
     (reduce (fn [ats ev]
               (apply-event ats ev)) attendees events))))


#_(let [attendees (attendees-by-email [{:email "x@y.be"
                                       :state :waiting}
                                     {:email "f@g.be"
                                      :state :missing}])
      events [{:type "INVITED" :args ["x@y.be"]}
              {:type "INVITED" :args ["f@g.be"]}]]
  (vals (reduce (fn [ats ev]
                  (apply-event ats ev)) attendees events)))
