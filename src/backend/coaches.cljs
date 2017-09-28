(ns backend.coaches
  (:require [attendomat.parse :as parse]
            [backend.sheets :as sh]))

(def blank-coach {:state :waiting :history []})

(defn parse-rows [[header & rows]]
  (->> (map #(parse/row->map header %) rows)
       (map #(merge blank-coach %))))

(defn read-spreadsheet-data [url]
  (let [spreadsheet (sh/open-by-url url)]
    (-> spreadsheet
        (sh/find-sheet "Form Responses 1")
        sh/data-range
        parse-rows)))
