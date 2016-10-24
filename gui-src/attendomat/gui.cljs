(ns attendomat.gui
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

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

(defn gui []
  [filters])

(r/render [gui] (js/document.getElementById "attendomat"))
