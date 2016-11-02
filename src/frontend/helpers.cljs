(ns frontend.helpers
  (:require [re-frame.core :as re-frame :refer [dispatch]]))

(defn on-change-handler
  "Default handler for when you just want an atom that tracks a text input."
  [atom]
  (fn [event]
    (reset! atom (.. event -target -value))))

(defn on-change-dispatch
  "Default handler that dispatches an event when a text box changes"
  [event-name]
  (fn [browser-event]
    (dispatch [event-name (.. browser-event -target -value)])))


(defn present? [x]
  (and x (not (empty? x))))
