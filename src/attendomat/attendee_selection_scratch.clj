(ns attendomat.attendee-selection-scratch
  (:require [clojure.data.csv :as csv]
            [clojure.string :as s]
            [clojure.string :as str]))

(comment
 (def dir "/home/arne/tmp/selection-process/")

 (def registered (read-string (slurp (str dir "attendees.edn"))))

 (defn read-tsv [name fields]
   (for [row (csv/read-csv
              (slurp (str dir name))
              :separator \tab)]
     (into {} (map vector fields row))))

 (defn read-csv [name fields]
   (for [row (csv/read-csv
              (slurp (str dir name))
              :separator \,)]
     (into {} (map vector fields row))))

 (str dir "w1-not-selected.tsv")
 (str dir "w2-attendees.tsv")
 (str dir "w3-accepted.tsv")
 (str dir "w3-attendees.tsv")


 (def w1-not-selected (read-tsv "w1-not-selected.tsv" [:first-name :last-name :email]))
 (def w2-attendees (read-tsv "w2-attendees.tsv" [:status :timestamp :first-name :last-name :email]))
 (def w2-accepted (filter #(= "ACCEPTED" (:status %)) w2-attendees))
 (def w2-not-selected (filter #(not= "ACCEPTED" (:status %)) w2-attendees))
 (def w3-accepted (read-tsv "w3-accepted.tsv" [:first-name :last-name :email]))
 (def w3-not-selected (read-tsv "w3-not-attending.tsv" [:first-name :last-name :email]))


 (count w1-not-selected) ;;=> 20

 (count w2-accepted) ;;=> 33
 (count w2-not-selected) ;;=> 36

 (count w3-accepted) ;;=> 36
 (count w3-not-selected) ;;=> 96

 (def not-selected (concat w1-not-selected w2-not-selected w3-not-selected))
 (def all-accepted (concat w2-accepted w3-accepted))

 (def not-selected-emails (set (map :email not-selected)))
 (def not-selected-names (set (map #(str (s/trim (:first-name % "")) " " (s/trim (:last-name % ""))) not-selected)))

 (def accepted-emails (set (map :email all-accepted)))
 (def accepted-names (set (map #(str (s/trim (:first-name % "")) " " (s/trim (:last-name % ""))) all-accepted)))

 (count (set not-selected-emails))

 (map :email (filter #(not-selected-emails (s/trim (:email %))) registered))

 (map :email (filter #(not-selected-names (str (s/trim (:first-name % "")) " " (s/trim (:last-name % "")))) registered))


 (doseq [email
         #{"drechenberger@hotmail.com" "chrlugk@gmx.de" "ta.kupler@posteo.de" "candy.bubble.h@gmail.com" "crodgarcia@gmail.com" "mail@felicitas-horstschaefer.de" "kawohl.bentodesign@gmail.com" "milarrocha@yahoo.com" "victoria.schabert@hotmail.de" "abugey@posteo.de" "holdthebeach@gmail.com"}]
   (println (str/join "\t" ["INVITED" email] )))

 (filter #(= (:email %) "candy.bubble.h@gmail.com") registered)
 ({:email "candy.bubble.h@gmail.com", :last-name "Ching Yi", :food-prefs "No", :age "18 and over | 18 und älter", :experience-other "No", :history [], :state :waiting, :first-name "Ho", :agree-coc "Yes | Ja", :assistance "No", :heard-of-us "My colleague - Franziska", :gender "F", :tshirt-size "S (straight cut / gerader Schnitt)", :timestamp #inst "2016-10-07T13:44:51.453-00:00", :experience-clojure "No | Nein", :language-prefs "English", :attended-before "No | Nein"})


 (frequencies (map :attended-before registered))
 )
