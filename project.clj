(defproject attendomat "0.1.0-SNAPSHOT"
  :license {:name "Mozilla Public License 2.0"
            :url "https://www.mozilla.org/en-US/MPL/2.0/"}

  :dependencies [[org.clojure/clojure "1.9.0-alpha13"]
                 [org.clojure/clojurescript "1.9.229"]
                 [reagent "0.6.0"]]

  :source-paths ["gas-src"]

  :plugins [[lein-cljsbuild "1.1.4"]]

  :clean-targets ^{:protect false} [:target-path :compile-path "resources/public/js/compiled"]

  :cljsbuild {:builds
              {:gui-dev
               {:source-paths ["gui-src"]
                :figwheel true
                :compiler {:main attendomat.gui
                           :optimizations :none
                           :asset-path "js/gui-dev"
                           :output-to "resources/public/js/gui-dev.js"
                           :output-dir "resources/public/js/gui-dev"}}
               :gui-prod
               {:source-paths ["gui-src"]
                :compiler {:main attendomat.gui
                           :optimizations :advanced
                           :output-to "export/gui.js"
                           :output-dir "target/gui"}}
               :main
               {:source-paths ["gas-src"]
                :compiler {:main attendomat.core
                           :optimizations :advanced
                           :output-to "export/Code.gs"
                           :output-dir "target/main"
                           :pretty-print false
                           :externs ["resources/gas.ext.js"]
                           :foreign-libs [{:file "gas-src/entry_points.js"
                                           :provides ["attendomat.entry-points"]}]}}}}

  :profiles
  {:dev
   {:dependencies [[figwheel-sidecar "0.5.7"]
                   [com.cemerick/piggieback "0.2.1"]]

    :plugins      [[lein-figwheel "0.5.7"]]}})
