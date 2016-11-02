(ns frontend.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [frontend.events]
              [frontend.subs]
              [frontend.views :as views]
              [frontend.config :as config]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "attendomat")))

(defn ^:export init []
  (js/console.log "Init called")
  (re-frame/dispatch-sync [:initialize-db])
  (re-frame/dispatch [:fetch-attendees])
  (dev-setup)
  (mount-root))
