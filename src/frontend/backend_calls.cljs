(ns frontend.backend-calls
  (:require [goog.object :as obj]
            [attendomat.transit :as t]))

(def run (obj/get js/google.script "run"))
(def withSuccessHandler (obj/get run "withSuccessHandler"))

(defn server-call [[fname & args] callback]
  (let [remote-call (obj/get (withSuccessHandler
                              (fn [ret]
                                (callback (t/read-transit ret))))
                             (name fname))]
    (apply remote-call args)))
