(ns we-owe.views.templates)

(defn user-link
  [user]
  [:a {:href (str "/user/" user)} user])

(defn add-debt-button
  []
  [:a#add-debt-btn.btn.btn-primary {:href "/debts/add"} [:i.icon-plus.icon-white] " Add a debt"])

(defn debts-list [debts balances]
  [:div#debts-list
   [:h1 "Debts"]
   [:ul
    (for [{:keys [lender debtor amount]} debts]
      [:li (str debtor " owes " lender " $" amount ".")])]
   [:h1 "Balances"]
   [:ul
    (for [[person amount] balances]
      [:li (user-link (name person)) (str ": $" amount)])]])

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

(defn output-form
  [fields & {:keys [values errors] :or [values {} errors {}]}]
  (for [[field label] fields]
    (let [type (if (= field :password) "password" "text")]
      (horizontal-input field label type (field values) (field errors)))))

(defn add-debt-form
  [& {:keys [debt errors] :or [debt {} errors {}]}]
  [:div
   [:h1 "Add a debt"]
   [:form#add-debt-form.form-horizontal {:method "POST" :action "/debts/add"}
    (output-form [[:from "Lender"]
                  [:to "Borrower"]
                  [:amount "Amount"]]
                 :values debt
                 :errors errors)
    [:div.control-group
     [:div.controls
      [:button.btn.btn-primary {:type "submit"} "Add a debt"]]]]])

