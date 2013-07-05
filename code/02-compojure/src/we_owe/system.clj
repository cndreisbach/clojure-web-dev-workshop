(ns we-owe.system
  (:require [we-owe.handler :refer [create-handler]]
            [ring.adapter.jetty :refer [run-jetty]]))

(defn system
  "Returns a new instance of the application."
  []
  (let [db (atom {:debts []})
        handler (create-handler db)]
    {:db db
     :handler handler
     :server-port 3002
     :server-join? false}))

(defn start
  "Performs side effects to initialize the system, acquire resources,
  and start it running. Returns an updated instance of the system."
  [system]
  (if-let [server (:server system)]
    (doto server
      (.start))
    (assoc system :server (run-jetty (:handler system)
                                     {:port (:server-port system)
                                      :join? (:server-join? system)}))))

(defn stop
  "Performs side effects to shut down the system and release its
  resources. Returns an updated instance of the system."
  [system]
  (doto (:server system)
    (.stop)))
