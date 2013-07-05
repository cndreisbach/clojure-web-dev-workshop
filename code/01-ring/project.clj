(defproject request-echo "0.1.0-SNAPSHOT"
  :description "Echo all requests back"
  :url "http://localhost:3001"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.1.8"]]
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler request-echo/handler
         :port 3001})
