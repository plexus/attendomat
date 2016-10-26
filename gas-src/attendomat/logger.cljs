(ns attendomat.logger)

(defn enable-logger-print!
  "Set *print-fn* to Logger.log"
  []
  (set! cljs.core/*print-newline* false)
  (set! cljs.core/*print-fn*
    (fn [& args]
      (.apply (.-log js/Logger) js/Logger (into-array args))))
  (set! cljs.core/*print-err-fn*
    (fn [& args]
      (.apply (.-error js/Logger) js/Logger (into-array args))))
  nil)
