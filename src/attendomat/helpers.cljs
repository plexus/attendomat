(ns attendomat.helpers)

(defn present? [x]
  (and x (not (empty? x))))
