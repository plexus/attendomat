(ns backend.routes
  (:require [backend.event-sourcing :as es]
            [backend.sheets :as sh]))

(defn ^:export attendee-data []
  (es/attendee-data))

(defn ^:export add-event [type args]
  (es/add-event type args)
  (attendee-data))

(defn ^:export create-invite-batch [attendees]
  (let [date (js/Date.)
        sheet-name (str "Batch " (.getDay date) "." (.getMonth date) " " (.getHours date) ":" (.getMinutes date)) ]
    (doseq [at attendees]
      (es/add-event "INVITED" [(:email at)]))
    (attendee-data)))
