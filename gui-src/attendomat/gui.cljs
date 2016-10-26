(ns attendomat.gui
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [goog.object :as obj]
            [cognitect.transit :as t]))


(enable-console-print!)

(defonce transit-writer (t/writer :json))
(defonce transit-reader (t/reader :json))

(defn read-transit [t]
  (t/read transit-reader t))

(defn write-transit [s]
  (t/write transit-writer s))

(defonce attendees (r/atom []))

(defn checkbox [{:keys [caption]}]
  (let [id (gensym "checkbox_")]
    [:label {:for id}
     [:input {:type "checkbox" :name id :id id}]
     caption]))

(defn filters []
  [:div
   [checkbox {:caption "show all"}]
   [checkbox {:caption "invited"}]
   [checkbox {:caption "accepted"}]
   [checkbox {:caption "cancelled"}]
   [checkbox {:caption "timeout"}]
   [checkbox {:caption "not invited"}]])

(defn server-call [fname callback]
  (let [remote-call (obj/get
                     (js/google.script.run.withSuccessHandler
                      (fn [ret] (callback (read-transit ret))))
                     (name fname))]
    (remote-call)))

(defn fetch-attendees []
  (server-call :attendeeData
               (fn [ad]
                 (reset! attendees ad))))

(defn gui []
  [:div
   [filters]
   [:button {:on-click #(fetch-attendees)} "Fetch!"]
   [:pre (prn-str @attendees)]])

(r/render [gui] (js/document.getElementById "attendomat"))
