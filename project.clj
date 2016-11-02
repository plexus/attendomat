(defproject attendomat "0.1.0-SNAPSHOT"
  :license {:name "Mozilla Public License 2.0"
            :url "https://www.mozilla.org/en-US/MPL/2.0/"}

  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [org.clojure/clojurescript "1.9.293"]
                 [com.cognitect/transit-cljs "0.8.239"]
                 [re-frame "0.8.0"]
                 [reagent "0.6.0"]
                 [garden "1.3.2"]]

  :plugins [[lein-cljsbuild "1.1.4"]]

  :clean-targets ^{:protect false} [:target-path :compile-path "resources/public/js/compiled"]

  :cljsbuild {:builds
              {:gui-dev
               {:source-paths ["src"]
                :figwheel {:on-jsload "frontend.core/mount-root"}
                :compiler {:main frontend.core
                           :optimizations :none
                           :asset-path "http://localhost:3449/js/gui-dev"
                           :output-to "resources/public/js/gui-dev.js"
                           :output-dir "resources/public/js/gui-dev"
                           :preloads [devtools.preload]
                           :foreign-libs [{:file "resources/entry_points.js"
                                           :provides ["attendomat.entry-points"]}]}}
               :gui-prod
               {:source-paths ["src"]
                :compiler {:main frontend.core
                           :optimizations :advanced
                           :output-to "export/gui.js"
                           :output-dir "target/gui"
                           :foreign-libs [{:file "resources/entry_points.js"
                                           :provides ["attendomat.entry-points"]}]}}
               :main
               {:source-paths ["src"]
                :compiler {:main backend.core
                           :optimizations :advanced
                           :output-to "export/Code.gs"
                           :output-dir "target/main"
                           :pretty-print false
                           :externs ["resources/gas.ext.js"]
                           :foreign-libs [{:file "resources/entry_points.js"
                                           :provides ["attendomat.entry-points"]}]}}}}

  :profiles
  {:dev
   {:dependencies [[figwheel-sidecar "0.5.7"]
                   [com.cemerick/piggieback "0.2.1"]
                   ;;[binaryage/dirac "RELEASE"]
                   [binaryage/devtools "0.8.2"]]

    ;; :repl-options {:init (do
    ;;                        (require 'dirac.agent)
    ;;                        (dirac.agent/boot!))
    ;;                :port 8230
    ;;                :nrepl-middleware [dirac.nrepl/middleware]}

    :plugins      [[lein-figwheel "0.5.7"]]}})
