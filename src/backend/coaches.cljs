(ns backend.coaches
  (:require [attendomat.parse :as parse]
            [backend.sheets :as sh]))

(defn parse-rows [[header & rows]]
  (map #(parse/row->map header %) rows))

(defn read-spreadsheet-data [url]
  (let [spreadsheet (sh/open-by-url url)]
    (-> spreadsheet
        (sh/find-sheet "Form Responses 1")
        sh/data-range
        parse-rows)))
