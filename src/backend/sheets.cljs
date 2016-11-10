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

(defn error-sheet []
  (find-or-create-sheet "*ERRORS*"))

(defn error! [& args]
  (append-row (error-sheet) args))

(defn update-sheet-data [sheet-name data]
  (let [sheet (find-or-create-sheet sheet-name)]
    (let [data-col-cnt (apply max (map count data))
          data-row-cnt (count data)
          col-cnt (max data-col-cnt (.getLastColumn sheet))
          row-cnt (max data-row-cnt (.getLastRow sheet))
          range (.getRange sheet 1 1 row-cnt col-cnt)
          values (clj->js (repeat row-cnt (repeat col-cnt nil)))]
      (prn data)
      (doall
       (map-indexed
        (fn [ridx row]
          (doall
           (map-indexed
            (fn [cidx v]
              (aset values ridx cidx v))
            row)))
        data))

      (.setValues range values))))

;; getRange(row, column, numRows, numColumns)
;; getLastRow()
;; getLastColumn()
