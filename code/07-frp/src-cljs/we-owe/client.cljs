(ns we-owe.client
  (:require [we-owe.views.templates :as templates]
            [goog.json :as gjson]
            [goog.dom.forms :as forms]
            [dommy.core :as dom]
            [ajax.core :refer [GET POST]]
            tailrecursion.javelin)
  (:require-macros [dommy.macros :refer [node sel1]]
                   [tailrecursion.javelin.macros :refer [cell]]))

(declare init-add-debt-form)
(declare init-add-debt-button)

(def debts
  (cell '[]))

(def balances
  (cell '[]))

(defn replace!
  "sel1 and node are macros, which we can't have inside a cell."
  [selector body]
  (dom/replace! (sel1 selector)
                (node body))
  (sel1 selector))

(def debts-list
  (cell (replace! :#debts-list (templates/debts-list debts balances))))

;; (def new-debts
;;   (cell '[]))

;; (defn show-new-debts [debts]
;;   (when (seq debts)
;;     (dom/show! (sel1 [:#new-debts :h1]))
;;     (dom/replace-contents! (sel1 [:#new-debts :ul])
;;                            (node (for [debt debts]
;;                                    [:li (str (:from debt) " -> " (:to debt) ": $" (:amount debt))])))))

;; (def new-debts-list
;;   (cell (show-new-debts new-debts)))

(defn update-debts [data]
  (reset! debts (:debts data))
  (reset! balances (:balances data)))

(defn- add-debt-form
  [debt errors]
  (node (templates/add-debt-form :debt debt :errors errors)))

(defn- add-debt-success
  [{:keys [ok debt errors location]}]
  (let [ok-handler
        (fn [data]
          ;; (swap! new-debts conj debt)
          (update-debts data)
          (dom/replace-contents! (sel1 :#add-debt-container)
                                 (templates/add-debt-button))
          (init-add-debt-button))]
    (if ok
      (GET "/debts.json"
           {:format :json
            :keywordize-keys true
            :handler ok-handler})
      (init-add-debt-form debt errors))))

(defn- add-debt-error [error-map]
  (.log js/console error-map))

(defn- add-debt-submit
  [event]
  (.preventDefault event)
  (let [form (forms/getFormDataMap (sel1 :#add-debt-form))
        debt {:amount (first (.get form "amount"))
              :from (first (.get form "from"))
              :to (first (.get form "to"))}]
    (POST "/debts/add.json"
          {:format :json
           :keywordize-keys true           
           :params debt
           :handler add-debt-success
           :error-handler add-debt-error})))

(defn- init-add-debt-form
  [debt errors]
  (dom/replace-contents! (sel1 :#add-debt-container)
                         (add-debt-form debt errors))
  (dom/listen! (sel1 :#add-debt-form) :submit add-debt-submit))

(defn- init-add-debt-button
  []
  (dom/listen! (sel1 :#add-debt-btn)
               :click
               (fn [event]
                 (.preventDefault event)
                 (init-add-debt-form {} {}))))

(defn main
  []
  (GET "/debts.json"
       {:format :json
        :keywordize-keys true
        :handler update-debts})
  (init-add-debt-button))

(cell (.log js/console (str "Debts: " debts)))
(cell (.log js/console (str "Balances: " balances)))
(main)
