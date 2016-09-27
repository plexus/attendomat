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


(defn ^:export popup-attendee-selector []
  (.. (spreadsheet-ui)
      (showModalDialog (.. js/HtmlService
                           (createHtmlOutputFromFile "attendee selector")
                           (setWidth 400)
                           (setHeight 300))
                       "Testing dialog thing"))

  #_(let [sheet (js/SpreadsheetApp.getActiveSheet)]
    (.appendRow sheet #js ["a" "b" "c"])))

  ;; var ss = SpreadsheetApp.openById("0Avt7ejriwlxudGZfV2xJUGJZLXktm2RhQU1uRUgtaXc");
  ;; var s = ss.getSheetByName("Database");
  ;; var data = s.getDataRange().getValues();

(defn format-name [att]
  (str
   (nth att 2) " "
   (nth att 3) " <"
   (nth att 4) ">"))

(defn ^:export attendee-list []
  (->> ;;(find-sheet "Form Responses 1")
   (active-sheet)
   data-range
   (drop 1)
   (map format-name)
   clj->js))

(defn ^:export create-menu []
  (.. (spreadsheet-ui)
      (createMenu "ClojureScript")
      (addItem "Do it", "popup_attendee_selector")
      (addToUi)))

      ;; .addSeparator()
      ;; .addSubMenu(ui.createMenu('Sub-menu')
      ;;     .addItem('Second item', 'menuItem2'))

#_
(#inst "2016-05-28T21:53:07.000-00:00" "" "Doreen" "Müller" "doreen" "weiblich" "English or German are both ok" "bin Vegetarierin" "" "" "Ruby (per codeacademy)\nStatistische Syntaxen: R, Stata, SPSS" "flt newsletter des frauenreferats der fu" "Yes | Ja" "Yes, you can take pictures of me. | Ja, Bilder von mir sind ok." "above 30 | Über 30" "No | Nein" "No | Nein")
