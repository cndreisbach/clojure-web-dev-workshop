(defproject we-owe "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://localhost:3002"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.1.8"]
                 [compojure "1.1.5"]]
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler we-owe.handler/ring-handler
         :port 3002}  
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.3"]
                                  [org.clojure/java.classpath "0.2.0"]
                                  [alembic "0.1.0"]
                                  [expectations "1.4.45"]]}})
