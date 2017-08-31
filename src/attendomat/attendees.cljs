(ns attendomat.attendees
  (:require [backend.sheets :as sh]))


(def fields
  {"Timestamp" :timestamp
   "First name" :first-name
   "Your first name" :first-name
   "Last name" :last-name
   "Your last name" :last-name
   "E-mail" :email
   "Age" :age
   "Your Age" :age
   "With what gender do you identify?" :gender
   "Can we take pictures of you and share them as Creative Commons?" :photo-clearance
   "Did you take part in a ClojureBridge workshop before?" :attended-before
   "Do you have any food allergies or dietary preferences? (e.g. vegan, vegetarian, GF)" :food-prefs
   "Do you need any kind of assistance, or have health issues you would like us to know about? " :assistance
   "Do you need support with child care during the event? (financial or at-the-event care)" :childcare
   "Have you tried any programming before? If yes, what sort of things? " :experience-other
   "Have you tried programming with Clojure before? " :experience-clojure
   "How did you hear about ClojureBridge Berlin? " :heard-of-us
   "I have read the Code of Conduct, and agree to honor it." :agree-coc
   "If we get T-Shirts, what would be your size?" :tshirt-size
   "Is there anything else you would like to mention?" :comment
   "Is there anything else you would like to mention? " :comment
   "Which language do you prefer?" :language-prefs
   "Do you plan to travel to Berlin from outside Berlin/Brandenburg for the workshop?" :travel})

(defn parse-attendee-data [[header & rows]]
  (map (fn [row]
         (into {:state :waiting
                :history []}
               (map-indexed (fn [idx heading]
                              (let [field (nth row idx)
                                    field-name (get fields heading)]

                                (when-not (= field "")
                                  (when (and (not (= heading "")) (nil? field-name))
                                    (sh/error! (str "No field mapping for \"" heading "\"")))
                                  [field-name field]))) header)))
       rows))

(defn filter-state [state attendees]
  (filter #(= state (:state %)) attendees))

(defn randomly-select [attendees state count]
  (->> attendees
       (filter-state state)
       shuffle
       (take count)))
