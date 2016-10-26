(ns frontend.views
    (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]))

(defn main-panel []
  (let [atts (subscribe [:attendees])]
    (fn []
      [:div
       (for [a @atts]
         [:div (:first-name a) " " (:last-name a)])
       #_[:pre (prn-str @atts)]])))
