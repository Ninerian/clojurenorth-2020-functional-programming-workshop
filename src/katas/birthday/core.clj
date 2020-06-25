(ns katas.birthday.core
  (:require
   [clojure.data.csv :as csv]
   [clojure.java.io :as io]
   [clojure.string :as string]
   [postal.core :as postal])
  (:import
   (java.time LocalDate)
   (java.time.format DateTimeFormatter)
   (java.time.temporal ChronoUnit)))

(def month-day (DateTimeFormatter/ofPattern "MM/dd"))
(def year-month-day (DateTimeFormatter/ofPattern "yyyy/MM/dd"))
(def connection {:host "localhost"
                 :user "azurediamond"
                 :pass "hunter2"
                 :port 2525})

(defn read-file
  [file]
  (io/reader (io/resource file)))

(defn file->csv
  [reader]
  (csv/read-csv reader))

(defn parse-csv
  [[header & content]]
  (map (partial zipmap (map keyword header)) content))

(def get-employees
  (comp parse-csv file->csv read-file))

(defn parse-date
  [date]
  (LocalDate/parse date year-month-day))

(defn get-today []
  (LocalDate/now))

(defn is-birthday
  [today date]
  (let [today' (.format today month-day)]
    (string/ends-with? date today')))

(defn get-year-diff
  [to from]
  (.between ChronoUnit/YEARS from to))

(defn get-age
  [today {:keys [date_of_birth]}]
  (get-year-diff today (parse-date date_of_birth)))

(defn add-age
  ""
  [today employee]
  (assoc employee :age (get-age today employee)))

(defn build-message
  [name age]
  (str "Happy Birthday " name "! " "Wow, you're " age " already!"))

(defn build-mail
  [{:keys [email name age]}]
  {:from "me@example.com"
   :to email
   :subject "Happy Birthday!"
   :body (build-message name age)})

(defn send-mail!
  [conn mail]
  (postal/send-message conn mail))

(defn greet! [source today mail-fn]
  (->>  source
        get-employees
        (filter #(is-birthday today (:date_of_birth %)))
        (map (partial add-age today))
        (map build-mail)
        (map mail-fn)))

(comment (greet! "birthday/employees.csv" (get-today) println))
