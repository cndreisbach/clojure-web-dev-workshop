(ns us.dreisbach.we-owe.debts-test
  (:use expectations
        us.dreisbach.we-owe.debts))

"Testing simplify function"

(expect {["alice" "bob"] 5.0}
        (simplify [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}]))
(expect {["alice" "bob"] 5.0, ["doug" "claire"] 3.0}
        (simplify [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}
                   {:from "claire" :to "doug" :amount 3.0 :when #inst "2013-01-03"}]))
(expect {["alice" "bob"] 7.0}
        (simplify [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}
                   {:from "bob" :to "alice" :amount 2.0 :when #inst "2013-01-03"}]))
(expect {["alice" "bob"] 2.0}
        (simplify [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}
                   {:from "alice" :to "bob" :amount 3.0 :when #inst "2013-01-03"}]))
(expect {["bob" "alice"] 2.0}
        (simplify [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}
                   {:from "alice" :to "bob" :amount 7.0 :when #inst "2013-01-03"}]))

"Testing balances function"

(= {"alice" -5.0, "bob" 5.0}
   (balances [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}]))
(= {"alice" -5.0, "bob" 5.0, "doug" -3.0, "claire" 3.0}
   (balances [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}
              {:from "claire" :to "doug" :amount 3.0 :when #inst "2013-01-03"}]))
(= {"alice" -7.0, "bob" 7.0}
   (balances [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}
              {:from "bob" :to "alice" :amount 2.0 :when #inst "2013-01-03"}]))
(= {"alice" -2.0, "bob" 2.0}
   (balances [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}
              {:from "alice" :to "bob" :amount 3.0 :when #inst "2013-01-03"}]))

"Testing add-debt function"

(= [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}
    {:from "alice" :to "bob" :amount 3.0 :when #inst "2013-01-03"}]
   (add-debt [{:from "bob" :to "alice" :amount 5.0 :when #inst "2013-01-02"}]
             {:from "alice" :to "bob" :amount 3.0 :when #inst "2013-01-03"}))
