(ns we-owe.views
  (:require [clojure.pprint :refer [pprint]]
            [we-owe.debts :refer [valid-debt? simplify balances]]
            [ring.util.response :refer [redirect-after-post]]
            [hiccup.core :refer :all]
            [hiccup.page :refer :all]
            [hiccup.element :refer :all]
            [hiccup.form :as form]
            [validateur.validation :refer :all]))

(defn- pstr [obj]
  (with-out-str (pprint obj)))

(defn- layout
  [& content]
  (html
   (html5 [:head
           [:meta {:charset "utf-8"}]
           [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
           (include-css "/css/bootstrap.min.css")
           [:style {:type "text/css"} "body { margin-top: 30px; }"]]
          [:body
           [:div.container
            [:div.navbar
             [:div.navbar-inner
              [:a.brand {:href "/"} "WeOwe"]]]
            content]
           (include-js "/js/bootstrap.min.js")])))

(defn index-page [db]
  (let [debts (:debts @db)]
    (layout
     [:h1 "Debts"]
     [:ul
      (for [[[debtor lender] amount] (simplify debts)]
        [:li (str debtor " owes " lender " $" amount ".")])]
     [:h1 "Balances"]
     [:ul
      (for [[person amount] (balances debts)]
        [:li (str person ": $" amount)])]
     [:div
      [:a.btn.btn-primary {:href "/add-debt"} [:i.icon-plus.icon-white] " Add a debt"]])))

(defn person-page [db person]
  (let [debts (simplify (:debts @db))
        owed (->> debts
                  (filter (fn [[[_ owed] amount]]
                            (= owed person)))
                  (map (fn [[[owes _] amount]] (vector owes amount))))
        owes (->> debts
                  (filter (fn [[[owes _] amount]]
                            (= owes person)))
                  (map (fn [[[_ owed] amount]] (vector owed amount))))]
    (layout
     [:h1 "You owe:"]
     [:ul
      (if (zero? (count owes))
        [:li "Nothing!"]
        (for [[person amount] owes]
          [:li (str person ": $" amount)]))]
     [:h1 "You are owed:"]
     [:ul
      (if (zero? (count owed))
        [:li "Nothing!"]
        (for [[person amount] owed]
          [:li (str person ": $" amount)]))])))

(defn- horizontal-input [field label value errors]
  (let [field (name field)
        field-id (str field "-field")]
    [:div {:class (if (seq errors) "control-group error" "control-group")}
     [:label.control-label {:for field-id} label]
     [:div.controls
      [:input {:id field-id :type "text" :name field :value value}]
      (if (seq errors)
        (for [error errors]
          [:span.help-block error]))]]))

(defn- output-form
  ([fields] (output-form fields {} {}))
  ([fields values errors]
     (for [[field label] fields]
       (horizontal-input field label (field values) (field errors)))))

(defn add-debt-page
  ([] (add-debt-page {} {}))
  ([debt errors]
     (layout
      [:h1 "Add a debt"]
      (form/form-to {:class "form-horizontal"} [:post "/add-debt"]
                    (output-form [[:from "Lender"]
                                  [:to "Borrower"]
                                  [:amount "Amount"]]
                                 debt
                                 errors)
                    [:div.control-group
                     [:div.controls [:button.btn.btn-primary {:type "submit"} "Add a debt"]]]))))

(defn add-debt-post [db debt]
  (let [debt-validator (validation-set
                        (presence-of :from)
                        (presence-of :to)
                        (presence-of :amount)
                        (format-of :amount :format #"^\d+$" :message "must be a number"))]
    (if (valid? debt-validator debt)
      (let [debt (update-in debt [:amount] #(Float/parseFloat %))]
        (swap! db update-in [:debts] conj debt)
        (redirect-after-post "/"))
      (let [errors (debt-validator debt)]
        (add-debt-page debt errors)))))
