(ns we-owe.views
  (:require [clojure.pprint :refer [pprint]]
            [we-owe.debts :refer [simplify balances]]))

(defn- pstr [obj]
  (with-out-str (pprint obj)))

(defn index-page [db]
  (let [debts (:debts @db)]
    (str "Balances:\n"
         (pstr (balances debts))
         "\n\nAll debts: \n"
         (pstr (simplify debts)))))

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
    (str "You owe:\n" (pstr owes)
         "\n\nYou are owed:\n" (pstr owed))))

