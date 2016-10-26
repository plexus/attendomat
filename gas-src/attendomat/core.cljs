(ns attendomat.core
  (:require [attendomat.entry-points]
            [attendomat.attendees :refer [parse-attendee-data]]
            [attendomat.sheets :as sh]
            [attendomat.logger :refer [enable-logger-print!]]
            [attendomat.transit :refer [write-transit]]))

(enable-logger-print!)

(defn ^:export on-open-hook [e]
  (println "Creating sidebar")
  (.. (sh/spreadsheet-ui)
      (showSidebar
       (.. (sh/html-file js/sidebarFile)
           (setTitle "ClojureBridge")))))

(defn ^:export on-edit-hook [e])

(defn ^:export attendee-data []
  (-> (sh/find-sheet "Form Responses 1")
      sh/data-range
      parse-attendee-data
      write-transit))
