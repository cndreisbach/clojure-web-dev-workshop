(ns us.dreisbach.we-owe.handler-test
  (:require [cheshire.core :as json]
            [us.dreisbach.we-owe.system :as system])
  (:use expectations
        ring.mock.request
        us.dreisbach.we-owe.handler))

(def system (system/system))
(def handler (:handler system))

(let [response (handler (-> (request :post "/add-debt.json")
                            (body (json/generate-string {:from "Clinton"
                                                         :to "Laura"
                                                         :amount 3.5}))
                            (content-type "application/json")))]
  (expect
   {"debt" {"from" "Clinton"
            "to" "Laura"
            "amount" 3.5}
    "ok" true}
   (in (json/parse-string (:body response)))))
