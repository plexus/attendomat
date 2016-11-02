(ns frontend.helpers)

(defn on-change-handler
  "Default handler for when you just want an atom that tracks a text input."
  [atom]
  (fn [event]
    (reset! atom (.. event -target -value))))
