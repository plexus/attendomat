(ns backend.sheets)

;; @return Spreadsheet
(defn active-spreadsheet []
  (.getActive js/SpreadsheetApp))

;; @return Sheet
(defn active-sheet []
  (.getActiveSheet js/SpreadsheetApp))

;; @return Spreadsheet
(defn open-by-url [url]
  (.openByUrl js/SpreadsheetApp url))

;; @return Sheet
(defn find-sheet
  ([spreadsheet name]
   (.getSheetByName spreadsheet name))
  ([name]
   (find-sheet (active-spreadsheet) name)))

(defn find-or-create-sheet [name]
  (if-let [sh (find-sheet name)]
    sh
    (.insertSheet (active-spreadsheet) name)))

(defn spreadsheet-ui []
  (.getUi js/SpreadsheetApp))

(defn data-range [sheet]
  (->> (.. sheet getDataRange getValues)
       array-seq
       (map array-seq)))

(defn append-row [sheet vals]
  (.appendRow sheet (clj->js vals)))

(defn html-file [name]
  (.createHtmlOutputFromFile js/HtmlService name))

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
