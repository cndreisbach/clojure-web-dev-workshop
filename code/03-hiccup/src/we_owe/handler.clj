(ns we-owe.handler
  (:require [clojure.pprint :refer [pprint]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [we-owe.views :as views]))

(defn create-routes [db]
  (routes
   (GET "/" [] (views/index-page db))
   (GET "/add-debt" [] (views/add-debt-page))
   (POST "/add-debt" [from to amount] (views/add-debt-post db {:from from :to to :amount amount}))
   (GET "/:person" [person] (views/person-page db person))
   (route/resources "/")
   (route/not-found "Page not found")))

(defn create-handler [db]
  (-> (create-routes db)
      handler/site))

(def ring-handler
  (create-handler
   (atom {:debts [{:from "clinton" :to "pete" :amount 3.50}
                  {:from "clinton" :to "diego" :amount 2.00}
                  {:from "pete" :to "clinton" :amount 1.25}
                  {:from "jill" :to "pete" :amount 10.00}]})))
