(ns backend.event-sourcing
  (:require [backend.sheets :as sh]))

(defn event-sheet []
  (sh/find-or-create-sheet "*EVENTS*"))

(defn event-data []
  (sh/data-range (event-sheet)))

(defn add-event [type args]
  (sh/append-row (event-sheet)
                 (into [(js/Date.) type] args)))
