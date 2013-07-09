(ns we-owe.handler
  (:require [clojure.pprint :refer [pprint]]
            [ring.middleware
             [stacktrace :refer [wrap-stacktrace]]
             [mime-extensions :refer [wrap-convert-extension-to-accept-header]]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [noir.response :as response]
            [noir.util.route :refer [restricted]]
            [noir.util.middleware :refer [app-handler]]
            [noir.session :as session]
            [we-owe.views :as views]
            [we-owe.resources :as resources]))

(defn- logged-in? [request]
  (session/get :user))

(defn create-routes [db]
  (routes
   (GET "/" [] (response/redirect "/debts" :permanent))
   (GET "/debts" [] resources/debts)
   (GET "/debts.json" [] resources/debts)   

   (ANY "/debts/add" [] (restricted resources/add-debt))
   (POST "/debts/add.json" [] resources/add-debt)

   (GET "/user/:user.json" [user] (restricted (resources/user user)))
   (GET "/user/:user" [user] (restricted (resources/user user)))

   (GET "/login" [] (views/login-page))
   (POST "/login" [username password]
         (views/login-post db {:username username :password password}))
   (ANY "/logout" [] (views/logout-page))

   (GET "/*.css" {{path :*} :route-params} (views/css-page path))

   (route/resources "/")
   (route/not-found "Page not found")))

(defn wrap-db [handler db]
  (fn [{:as request}]
    (-> request
        (assoc :db db)
        handler)))

(defn create-handler [db]
  (-> (app-handler
       [(create-routes db)]
       :access-rules [{:redirect "/login"
                       :rules [logged-in?]}])
      (wrap-db db)
      (wrap-convert-extension-to-accept-header)
      (wrap-stacktrace)))

(def ring-handler
  (create-handler
   (atom {:debts [{:from "Clinton" :to "Pete" :amount 3.50}
                  {:from "Clinton" :to "Diego" :amount 2.00}
                  {:from "Pete" :to "Clinton" :amount 1.25}
                  {:from "Jill" :to "Pete" :amount 10.00}]})))
