(defproject we-owe "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-cljsbuild "0.3.2"]
            [lein-ring "0.8.3"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.1.8"]
                 [ring.middleware.mime-extensions "0.2.0"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.3"]
                 [com.novemberain/validateur "1.4.0"]
                 [cheshire "5.2.0"]
                 [garden "0.1.0-beta5"]
                 [lib-noir "0.6.4"]
                 [liberator "0.9.0"]

                 ;; ClojureScript deps
                 [prismatic/dommy "0.1.1"]
                 [cljs-ajax "0.1.3"]]
  :ring {:handler we-owe.handler/ring-handler
         :port 3006}
  :cljsbuild {:crossovers [we-owe.views.templates]
              :builds [{:source-paths ["src-cljs"]
                        :compiler {:pretty-print true
                                   :output-to "resources/public/js/main.js"
                                   :optimizations :whitespace}}]}
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.3"]
                                  [org.clojure/java.classpath "0.2.0"]
                                  [alembic "0.1.0"]
                                  [ring-mock "0.1.5"]]}})
