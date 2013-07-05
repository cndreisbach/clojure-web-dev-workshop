(ns we-owe.handler
  (:require [clojure.pprint :refer [pprint]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [we-owe.views :as views]))

(defn create-routes [db]
  (routes
   (GET "/" [] (views/index-page db))
   (GET "/:person" [person] (views/person-page db person))
   (route/not-found "Page not found")))

(defn wrap-plain-text
  [handler]
  (fn [req]
    (assoc-in (handler req) [:headers "Content-Type"] "text/plain;charset=UTF-8")))

(defn create-handler [db]
  (-> (create-routes db)
      handler/site
      wrap-plain-text))

(def ring-handler
  (create-handler
   (atom {:debts [{:from "clinton" :to "pete" :amount 3.50}
                  {:from "clinton" :to "diego" :amount 2.00}
                  {:from "pete" :to "clinton" :amount 1.25}
                  {:from "jill" :to "pete" :amount 10.00}]})))
