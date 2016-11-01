(ns backend.core
  (:require [attendomat.transit :refer [read-transit write-transit]]
            [attendomat.entry-points]
            [backend.routes]
            [backend.logger :refer [enable-logger-print!]]
            [backend.sheets :as sh]
            [goog.object :as obj]))

(enable-logger-print!)

(defn ^:export on-open-hook [e]
  (println "Creating sidebar")
  (.. (sh/spreadsheet-ui)
      (showSidebar
       (.. (sh/html-file js/sidebarFile)
           (setTitle "ClojureBridge")))))

(defn ^:export on-edit-hook [e])

(defn ^:export backend-call [name args]
  (let [args (read-transit args)]
    (prn "backend-call" [name args])

    (let [f (js/eval (str "backend.routes." name))]
      (write-transit
       (if f
         (apply f args)
         [:error (str "There is no " (prn-str name) " function in the backend.routes namespace.")])))))
