(ns we-owe.handler
  (:require [clojure.pprint :refer [pprint]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [noir.response :as response]
            [noir.util.route :refer [restricted]]
            [noir.util.middleware :refer [app-handler]]
            [noir.session :as session]
            [we-owe.views :as views]))

(defn- logged-in? [request]
  (session/get :user))

(defn create-routes [db]
  (routes
   (GET "/" [] (response/redirect "/debts" :permanent))

   (GET "/debts" [] (views/index-page db))
   (GET "/debts.json" [] (views/index-json db))

   (GET "/add-debt" [] (restricted (views/add-debt-page)))
   (POST "/add-debt" [from to amount]
         (restricted (views/add-debt-post db {:from from :to to :amount amount})))
   (POST "/add-debt.json" {body :body} (views/add-debt-json db (slurp body)))   

   (GET "/user/:person.json" [person] (restricted (views/person-json db person)))
   (GET "/user/:person" [person] (restricted (views/person-page db person)))
   
   (GET "/login" [] (views/login-page))
   (POST "/login" [username password]
         (views/login-post db {:username username :password password}))
   (ANY "/logout" [] (views/logout-page))

   (GET "/*.css" {{path :*} :route-params} (views/css-page path))   
   
   (route/resources "/")
   (route/not-found "Page not found")))

(defn create-handler [db]
  (app-handler
   [(create-routes db)]
   :access-rules [{:redirect "/login"
                   :rules [logged-in?]}]))

(def ring-handler
  (create-handler
   (atom {:debts [{:from "Clinton" :to "Pete" :amount 3.50}
                  {:from "Clinton" :to "Diego" :amount 2.00}
                  {:from "Pete" :to "Clinton" :amount 1.25}
                  {:from "Jill" :to "Pete" :amount 10.00}]})))
