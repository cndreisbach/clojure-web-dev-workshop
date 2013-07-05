(ns we-owe.client
  (:require [we-owe.views.templates :as templates]
            [goog.json :as gjson]
            [goog.dom.forms :as forms]
            [dommy.core :as dom]
            [ajax.core :refer [GET POST]])
  (:use-macros [dommy.macros :only [node sel1]]))

(declare init-add-debt-form)

(defn- add-debt-form
  [debt errors]
  (node (templates/add-debt-form :debt debt :errors errors)))

(defn- add-debt-success
  [{:keys [ok debt errors location]}]
  (let [ok-handler
        (fn [{:keys [debts balances]}]
          (dom/replace! (sel1 :#debts-list)
                        (node (templates/debts-list debts balances)))
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
        params {:amount (first (.get form "amount"))
                :from (first (.get form "from"))
                :to (first (.get form "to"))}]
    (POST "/debts/add.json"
          {:format :json
           :keywordize-keys true           
           :params params
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
  (init-add-debt-button))

(main)
