(ns user
  (:require [alembic.still :refer [distill]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [clojure.repl :refer :all]
            [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [ring.mock.request :refer [request]]
            [we-owe.system :as system]
            [we-owe.debts :as debts]))

(def system nil)

(def debts [{:from "Clinton" :to "Pete" :amount 3.50}
            {:from "Clinton" :to "Diego" :amount 2.00}
            {:from "Pete" :to "Clinton" :amount 1.25}
            {:from "Jill" :to "Pete" :amount 10.00}])

(defn init
  "Constructs the current dev system."
  []
  (alter-var-root #'system
                  (constantly (system/system))))

(defn start
  "Starts the current development system."
  []
  (alter-var-root #'system system/start))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'system
    (fn [s] (when s (system/stop s)))))

(defn go
  "Initializes the current development system and starts it running."
  []
  (init)
  (start)
  (swap!
   (:db system)
   assoc :debts debts)
  )

(defn reset []
  (stop)
  (refresh :after 'user/go))

(comment
  (reset)
  )
