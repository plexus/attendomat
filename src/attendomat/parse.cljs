(ns attendomat.parse
  (:require [backend.sheets :as sh]))

(def fields
  {"Timestamp" :timestamp

   "First name" :first-name
   "Your first name" :first-name

   "Last name" :last-name
   "Your last name" :last-name

   "Email Address" :email
   "E-mail" :email

   "Age" :age
   "Your Age" :age

   "What gender do you identify with?" :gender
   "With what gender do you identify?" :gender

   "Can we take pictures of you and share them as Creative Commons?" :photo-clearance
   "Did you take part in a ClojureBridge workshop before?" :attended-before
   "Do you have any food allergies or dietary preferences? (e.g. vegan, vegetarian, GF)" :food-prefs
   "Do you need any kind of assistance, or have health issues you would like us to know about? " :assistance
   "Do you need support with child care during the event? (financial or at-the-event care)" :childcare
   "Have you tried any programming before? If yes, what sort of things? " :experience-other
   "Have you tried programming with Clojure before? " :experience-clojure
   "How much Clojure experience do you have?" :experience-clojure
   "How did you hear about ClojureBridge Berlin? " :heard-of-us
   "I have read the Code of Conduct, and agree to honor it." :agree-coc
   "I have read and agree with the Berlin Code of Conduct " :agree-coc
   "If we get T-Shirts, what would be your size?" :tshirt-size
   "Is there anything else you would like to mention?" :comment
   "Is there anything else you would like to mention? " :comment
   "Anything else you like to mention?" :comment
   "Which language do you prefer?" :language-prefs
   "Which languages can you coach in?" :language-prefs
   "Do you plan to travel to Berlin from outside Berlin/Brandenburg for the workshop?" :travel

   ;; COACHES
   "Who would you prefer to coach for?" :coaching-prefs
   "Would you be okay with being a floating coach?" :floating-coach?
   "What is your background? Which languages do you know well besides Clojure?" :other-langs
   "Did you coach before? And if so, where?" :experience-coaching
   "Phone number" :phone})

(defn row->map [header row]
  (into {} (map-indexed (fn [idx heading]
                          (let [field (nth row idx)
                                field-name (get fields heading)]

                            (when-not (= field "")
                              (when (and (not (= heading "")) (nil? field-name))
                                (sh/error! (str "No field mapping for \"" heading "\"")))
                              [field-name field]))) header)))
