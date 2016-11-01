(ns backend.routes
  (:require [attendomat.attendees :refer [parse-attendee-data]]
            [backend.event-sourcing :as es]
            [backend.sheets :as sh]))

(defn ^:export attendee-data []
  (-> (sh/find-sheet "Form Responses 1")
      sh/data-range
      parse-attendee-data))

(defn ^:export add-event [type args]
  (es/add-event type args))
