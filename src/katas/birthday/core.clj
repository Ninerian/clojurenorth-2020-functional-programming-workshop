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


(defn read-file
  [file]
  (io/reader (io/resource file)))

(defn file->csv
  ""
  [reader]
  (csv/read-csv reader))

(defn parse-csv
  ""
  [[header & content]]
  (map (partial zipmap (map keyword header )) content))

(comment
 (file->csv  (read-file "birthday/employees.csv"))

  (->>  "birthday/employees.csv"
        read-file
        file->csv
        parse-csv
       ;; rest
       )
)

(defn greet! []
  (->>  "birthday/employees.csv"
        read-file
        file->csv
       rest
       (filter (fn [row]
                 (string/ends-with?
                   (row 2)
                   (.format (LocalDate/now) (DateTimeFormatter/ofPattern "MM/dd")))))
       (map (fn [row]
                 (postal/send-message
                  {:host "localhost"
                   :user "azurediamond"
                   :pass "hunter2"
                   :port 2525}
                  {:from "me@example.com"
                   :to (row 1)
                   :subject "Happy Birthday!"
                   :body (str "Happy Birthday " (row 0) "! "
                              "Wow, you're "
                              (.between ChronoUnit/YEARS
                                        (LocalDate/parse (row 2)
                                                         (DateTimeFormatter/ofPattern "yyyy/MM/dd"))
                                        (LocalDate/now))
                              " already!")})))
       doall))
