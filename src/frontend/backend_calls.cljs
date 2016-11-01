(ns frontend.backend-calls
  (:require [goog.object :as obj]
            [attendomat.transit :as t]
            [clojure.string :as str]))

(def run (obj/get js/google.script "run"))
(def withSuccessHandler (obj/get run "withSuccessHandler"))

(defn server-call [[fname & args] callback]
  (let [return-handler (fn [ret]
                         (callback (t/read-transit ret)))
        backend-call (-> (withSuccessHandler return-handler)
                         (obj/get "backendCall"))
        fname (str/replace (name fname) "-" "_")]

    (println fname (t/write-transit (if (nil? args) '() args)))

    (backend-call fname (t/write-transit (if (nil? args) '() args)))))
