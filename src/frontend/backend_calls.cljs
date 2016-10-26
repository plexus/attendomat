(ns frontend.backend-calls
  (:require [goog.object :as obj]
            [attendomat.transit :as t]))

(defn server-call [[fname & args] callback]
  (let [remote-call (obj/get
                     (js/google.script.run.withSuccessHandler
                      (fn [ret] (callback (t/read-transit ret))))
                     (name fname))]
    (apply remote-call args)))
