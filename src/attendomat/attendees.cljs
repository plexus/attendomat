(ns attendomat.attendees
  (:require [attendomat.parse :as parse]
            [backend.sheets :as sh]))

(def blank-attendee {:state :waiting :history []})

(defn parse-attendee-data [[header & rows]]
  (->> rows
       (map #(parse/row->map header %))
       (map #(merge blank-attendee %))))

(defn filter-state [state attendees]
  (filter #(= state (:state %)) attendees))

(defn randomly-select [attendees state count]
  (->> attendees
       (filter-state state)
       shuffle
       (take count)))
