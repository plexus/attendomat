(ns attendomat.helpers)

(defn present? [x]
  (and x (and (or (string? x) (seq? x) (seqable? x))
              (not (empty? x)))))
