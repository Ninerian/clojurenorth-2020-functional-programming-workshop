(ns katas.test.birthday
  (:require
    [clojure.test :refer :all]
    [katas.birthday.core :as birthday]))

(deftest parse-csv
  (testing "Convert list of values to a map"
    (is (= [{:foo "bar"}] (birthday/parse-csv [["foo"] ["bar"]])))
    (is (= [{:foo "bar" :baz 1}] (birthday/parse-csv [["foo" "baz"] ["bar" 1]])))
    ))

(deftest greet!
  (testing "sends email to everyone with birthday today"
    ;; How do we test???
    #_(greet!)))
