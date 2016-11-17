(ns backend.email
  (:require [clojure.string :as str]))

(defn search [query]
  (js/GmailApp.search query))

(defn messages [thread]
  (.getMessages thread))

(defn normalize-email [email]
  (-> (if-let [[_ email] (re-find #"<(.*)>" email)] ; "John Doe <John.Doe@foo.com>"
        email
        email)
      str/lower-case
      str/trim))

(defn from= [email message]
  (= (normalize-email email) (normalize-email (.getFrom message))))

(defn filter-from [email messages]
  (filter #(from= email %) messages))

(defn messages-from [email]
  (->> (search (str "from:" email))
       (mapcat messages)
       (filter-from email)
       (map (fn [m]
              {:id (.getId m)
               :date (.getDate m)
               :subject (.getSubject m)
               :from (.getFrom m)
               :to (.getTo m)
               :cc (.getCc m)
               :body (.getPlainBody m)}))))
