(ns request-echo
  (:require [clojure.pprint :refer [pprint]]))

(defn handler
  "Return the request as HTML."
  ;; A request comes in the handler.  
  [request]
  
  ;; The handler returns a response map.
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str "<h1>Request Echo</h1><pre>"
              (with-out-str (pprint request))
              "</pre>")})
