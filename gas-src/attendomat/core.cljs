(ns attendomat.core
  (:require [attendomat.entry-points]))

(def sheet-app js/SpreadsheetApp)

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

;; Sheet
;; setColumnWidth(columnPosition, width)
;; setRowHeight(rowPosition, height)
;; getRange(row, column, numRows, numColumns)

;; Range
;; setBackground(color)
;; getBackground(color)

(def ROWS 10)
(def COLS 10)

(defn initial-state [])

(def state (atom (initial-state)))

(defn ^:export resize-cells []
  (let [sheet (active-sheet)]
    (run! (fn [i] (.setRowHeight (js/SpreadsheetApp.getActiveSheet) i 5)) (range ROWS))
    #_(run! (fn [i] (.setColumnWidth sheet i 5)) (range COLS))))
