(ns attendomat.attendees
  (:require [attendomat.parse :as parse]
            [backend.sheets :as sh]))

(def blank-attendee {:state :waiting :history []})

(defn parse-attendee-data [[header & rows]]
  (map (fn [row]
         (merge blank-attendee (parse/row->map header row)))
       rows))

(defn filter-state [state attendees]
  (filter #(= state (:state %)) attendees))

(defn randomly-select [attendees state count]
  (->> attendees
       (filter-state state)
       shuffle
       (take count)))
