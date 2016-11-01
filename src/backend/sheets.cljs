(ns backend.sheets)

(def sheet-app js/SpreadsheetApp)
(def html-service js/HtmlService)

;; @return Spreadsheet
(defn active-spreadsheet []
  (.getActive sheet-app))

;; @return Sheet
(defn active-sheet []
  (.getActiveSheet sheet-app))

;; @return Sheet
(defn find-sheet [name]
  (.getSheetByName (active-spreadsheet) name))

(defn find-or-create-sheet [name]
  (if-let [sh (find-sheet name)]
    sh
    (.insertSheet (active-spreadsheet) name)))

(defn spreadsheet-ui []
  (.getUi sheet-app))

(defn data-range [sheet]
  (->> (.. sheet getDataRange getValues)
       array-seq
       (map array-seq)))

(defn append-row [sheet vals]
  (.appendRow sheet (clj->js vals)))

(defn html-file [name]
  (.createHtmlOutputFromFile html-service name))
