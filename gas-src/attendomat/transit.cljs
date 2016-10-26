(ns attendomat.transit
  (:require [cognitect.transit :as t]))

(defonce transit-writer (t/writer :json))
(defonce transit-reader (t/reader :json))

(defn read-transit [t]
  (t/read transit-reader t))

(defn write-transit [s]
  (t/write transit-writer s))
