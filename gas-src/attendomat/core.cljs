(ns attendomat.core
  (:require [attendomat.entry-points]
            [cognitect.transit :as t]))

(def sheet-app js/SpreadsheetApp)
(def html-service js/HtmlService)

(defn enable-logger-print!
  "Set *print-fn* to Logger.log"
  []
  (set! cljs.core/*print-newline* false)
  (set! cljs.core/*print-fn*
    (fn [& args]
      (.apply (.-log js/Logger) js/Logger (into-array args))))
  (set! cljs.core/*print-err-fn*
    (fn [& args]
      (.apply (.-error js/Logger) js/Logger (into-array args))))
  nil)

(enable-logger-print!)

(defonce transit-writer (t/writer :json))
(defonce transit-reader (t/reader :json))

(defn read-transit [t]
  (t/read transit-reader t))

(defn write-transit [s]
  (t/write transit-writer s))

;; @return Spreadsheet
(defn active-spreadsheet []
  (.getActive sheet-app))

;; @return Sheet
(defn active-sheet []
  (.getActiveSheet sheet-app))

;; @return Sheet
(defn find-sheet [name]
  (.getSheetByName (active-spreadsheet) name))

(defn spreadsheet-ui []
  (.getUi sheet-app))

(defn data-range [sheet]
  (->> (.. sheet getDataRange getValues)
       array-seq
       (map array-seq)))

(defn html-file [name]
  (.createHtmlOutputFromFile html-service name))

(defn ^:export on-open-hook [e]
  (println "Creating sidebar")
  (.. (spreadsheet-ui)
      (showSidebar
       (.. (html-file js/sidebarFile)
           (setTitle "ClojureBridge")
           (setWidth 300)))))

(defn ^:export on-edit-hook [e])

(defn ^:export attendee-data []
  (write-transit (data-range (find-sheet "Form Responses 1"))))
