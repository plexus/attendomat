(ns names
  (:require [clojure.string :as str]))


;; http://www2.census.gov/topics/genealogy/1990surnames/dist.female.first
(def female-names (map (comp str/capitalize first #(str/split % #"\s+")) (str/split (slurp "/home/arne/tmp/dist.female.first") #"\n")))
;; http://www2.census.gov/topics/genealogy/1990surnames/dist.all.last
(def last-names (map (comp str/capitalize first #(str/split % #"\s+")) (str/split (slurp "/home/arne/tmp/dist.all.last") #"\n")))

(def domains ["gmx.de" "gmail.com" "yahoo.com" "mailbox.net" "example.com" "hotmail.com"])

(defn name-seq []
  (cons [(rand-nth female-names) (rand-nth last-names)]
        (lazy-seq
         (name-seq))))

(run! println
      (take 32
            (map (fn [[first last]]
                   (str/join "\t" [(str/lower-case (str first "." last "@" (rand-nth domains))) first last ]))
                 (name-seq))))
