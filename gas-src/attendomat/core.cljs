(ns attendomat.core
  (:require [attendomat.entry-points]))

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

(defn spreadsheet-ui []
  (.getUi sheet-app))

(defn data-range [sheet]
  (->> (.. sheet getDataRange getValues)
       array-seq
       (map array-seq)))

(defn html-file [name]
  (.createHtmlOutputFromFile html-service name))

(defn ^:export on-open-hook [e]
  (.. (spreadsheet-ui)
      (showSidebar
       (.. (html-file js/sidebarFile)
           (setTitle "ClojureBridge")
           (setWidth 300)))))

;; function onOpen() {
;;   SpreadsheetApp.getUi() // Or DocumentApp or FormApp.
;;       .createMenu('Custom Menu')
;;       .addItem('Show sidebar', 'showSidebar')
;;       .addToUi();
;; }

;; function showSidebar() {
;;   var html = HtmlService.createHtmlOutputFromFile('Page')
;;       .setTitle('My custom sidebar')
;;       .setWidth(300);
;;   SpreadsheetApp.getUi() // Or DocumentApp or FormApp.
;;       .showSidebar(html);
;; }
