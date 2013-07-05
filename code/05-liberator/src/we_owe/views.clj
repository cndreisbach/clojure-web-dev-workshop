(ns we-owe.views
  (:require [clojure.pprint :refer [pprint]]
            [clojure.walk :refer [keywordize-keys]]
            [clojure.java.io :as io]            
            [we-owe.debts :as debts :refer [valid-debt? all-users]]
            [ring.util.response :refer [redirect-after-post]]
            [hiccup.core :refer :all]
            [hiccup.page :refer :all]
            [hiccup.element :refer :all]
            [hiccup.form :as form]
            [validateur.validation :refer :all]
            [cheshire.core :as json]
            [garden.core :refer [css]]
            [noir.response :as response]
            [noir.session :as session]))

(defn- pstr
  [obj]
  (with-out-str (pprint obj)))

(defn- user-link
  [user]
  [:a {:href (str "/user/" user)} user])

(defn- login-nav
  []
  (let [user (session/get :user)]
    (if user
      [:ul.nav.pull-right
       [:li.divider-vertical]                   
       [:li [:a {:href (str "/user/" user)}
             (str "Logged in as " user)]]
       [:li.divider-vertical]
       [:li [:a {:href "/logout"} "Logout"]]]
      [:ul.nav.pull-right
       [:li.divider-vertical]                   
       [:li [:a {:href "/login"} "Login"]]])))

(defn- layout
  [& content]
  (html
   (html5 [:head
           [:meta {:charset "utf-8"}]
           [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
           (include-css "/css/bootstrap.min.css")
           (include-css "/css/style.css")
           [:style {:type "text/css"} "body { margin-top: 30px; }"]]
          [:body
           [:div.container
            [:div.navbar
             [:div.navbar-inner
              [:a.brand {:href "/debts"} "WeOwe"]
              (login-nav)]]
            content]
           (include-js "/js/bootstrap.min.js")])))

(defmulti debts :format)

(defmethod debts "text/html"
  [{:keys [debts balances]}]
  (layout
   [:h1 "Debts"]
   [:ul
    (for [[[debtor lender] amount] debts]
      [:li (user-link debtor) (str " owes " lender " $" amount ".")])]
   [:h1 "Balances"]
   [:ul
    (for [[person amount] balances]
      [:li (user-link person) (str ": $" amount)])]
   [:div
    [:a.btn.btn-primary {:href "/debts/add"} [:i.icon-plus.icon-white] " Add a debt"]]))

(defmethod debts "application/json"
  [{:keys [debts balances]}]
  (let [debts (map (fn [[[to from] amount]]
                     {:lender from :debtor to :amount amount})
                   debts)]
    {:debts debts :balances balances}))

(defmulti user :format)

(defmethod user "text/html"
  [{:keys [debts owed owes user]}]
  (layout
   [:h1 (str user " owes:")]
   [:ul
    (if (zero? (count owes))
      [:li "Nothing!"]
      (for [[user amount] owes]
        [:li (user-link user) (str ": $" amount)]))]
   [:h1 (str user " is owed:")]
   [:ul
    (if (zero? (count owed))
      [:li "Nothing!"]
      (for [[user amount] owed]
        [:li (user-link user) (str ": $" amount)]))]))

(defmethod user "application/json"
  [{:keys [debts owed owes user]}]
  {:debts owes :loans owed})

(defn- horizontal-input
  [field label type value errors]
  (let [field (name field)
        field-id (str field "-field")]
    [:div {:class (if (seq errors) "control-group error" "control-group")}
     [:label.control-label {:for field-id} label]
     [:div.controls
      [:input {:id field-id :type type :name field :value value}]
      (if (seq errors)
        (for [error errors]
          [:span.help-block error]))]]))

(defn- output-form
  ([fields] (output-form fields {} {}))
  ([fields values errors]
     (for [[field label] fields]
       (let [type (if (= field :password) "password" "text")]
         (horizontal-input field label type (field values) (field errors))))))

(defn add-debt
  ([] (add-debt {} {}))
  ([debt errors]
     (layout
      [:h1 "Add a debt"]
      (form/form-to {:class "form-horizontal"} [:post "/debts/add"]
                    (output-form [[:from "Lender"]
                                  [:to "Borrower"]
                                  [:amount "Amount"]]
                                 debt
                                 errors)
                    [:div.control-group
                     [:div.controls
                      [:button.btn.btn-primary {:type "submit"} "Add a debt"]]]))))

(defn add-debt-json
  [db body]
  (let [debt (-> body
                 json/parse-string
                 keywordize-keys)
        debt-validator (validation-set
                        (presence-of :from)
                        (presence-of :to)
                        (presence-of :amount)
                        (numericality-of :amount))]
    (if (valid? debt-validator debt)
      (do (swap! db update-in [:debts] conj debt)
          (response/status
           201
           (response/json {:debt debt :debts (:debts @db) :ok true})))
      (response/status
       400
       (response/json {:ok false :errors (debt-validator debt)})))))

(defn login-page
  ([] (login-page {} {}))
  ([credentials errors]
     (layout
      [:h1 "Login"]
      (form/form-to {:class "form-horizontal"} [:post "/login"]
                    (output-form [[:username "Name"]
                                  [:password "Password"]]
                                 credentials
                                 errors)
                    [:div.control-group
                     [:div.controls
                      [:button.btn.btn-primary {:type "submit"} "Login"]]]))))

(defn login-post
  [db credentials]
  (let [legal-users (all-users (:debts @db))
        login-validator (validation-set
                         (presence-of :username)
                         (inclusion-of :username :in legal-users)
                         (presence-of :password)
                         (length-of :password :within (range 4 100)))]
    (if (valid? login-validator credentials)
      (do
        (session/put! :user (:username credentials))
        (redirect-after-post "/debts"))
      (let [errors (login-validator credentials)]
        (login-page credentials errors)))))

(defn logout-page
  []
  (session/remove! :user)
  (redirect-after-post "/debts"))

(defn css-page [path]
  (when-let [garden-url (io/resource (str "public/" path ".garden"))]
    (let [garden-data (load-file (.getPath garden-url))]
      {:status 200
       :headers {"Content-Type" "text/css"}
       :body (css garden-data)})))

(def css-page-memo (memoize css-page))
