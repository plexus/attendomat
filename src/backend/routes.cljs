(ns backend.routes
  (:require [attendomat.attendees :refer [filter-state]]
            [attendomat.helpers :refer [present?]]
            [backend.event-sourcing :as es]
            [backend.sheets :as sh]))

(defn ^:export attendee-data []
  (es/attendee-data))

(defn ^:export add-event [type args]
  (es/add-event type args)
  (attendee-data))

(defn ^:export create-invite-batch [attendees]
  (let [date (js/Date.)
        sheet-name (str "Batch " (.getDay date) "." (.getMonth date) " " (.getHours date) ":" (.getMinutes date)) ]
    (doseq [at attendees]
      (es/add-event "INVITED" [(:email at)]))
    (attendee-data)))

(defn summarize-field [field attendees]
  (->> attendees
       (filter (comp present? field))
       (map (juxt :first-name :last-name field))))

(defn stats-field [field attendees]
  (map vec (frequencies (map field attendees))))

(defn ^:export summarize []
  (let [attendees (attendee-data)
        accepted (filter-state :accepted attendees)
        cols [:first-name :last-name :email :language-prefs
              :age :gender :attended-before :experience-other
              :experience-clojure :heard-of-us :comment]]
    (for [type [:waiting :invited :accepted :cancelled]]
      (sh/update-sheet-data (name type)
                            (into [(map name cols)]
                                  (map (apply juxt cols)
                                       (filter-state type attendees)))))

    (sh/update-sheet-data "Food/Assistance/Childcare"
                          `[["--- THIS SHEET ONLY CONTAINS INFORMATION FROM SELECTED ATTENDEES ---"]
                            []
                            ["FOOD PREFERENCES"]
                            ~@(summarize-field :food-prefs accepted)
                            []
                            ["SPECIAL ASSISTANCE"]
                            ~@(summarize-field :assistance accepted)
                            []
                            ["CHILDCARE"]
                            ~@(summarize-field :childcare accepted)])

    (sh/update-sheet-data "Feedback"
                          `[["HOW DID YOU HEAR FROM US"]
                            ~@(summarize-field :heard-of-us attendees)
                            []
                            ["COMMENTS"]
                            ~@(summarize-field :comment attendees)
                            []])

    (sh/update-sheet-data "Stats"
                          `[["---- ALL ATTENDEES ----"]
                            []
                            ["AGE"]
                            ~@(stats-field :age attendees)
                            []
                            ["LANGUAGE PREFERENCE"]
                            ~@(stats-field :language-prefs attendees)
                            []
                            ["CLOJURE EXPERIENCE"]
                            ~@(stats-field :experience-clojure attendees)
                            []
                            ["ATTENDED BEFORE"]
                            ~@(stats-field :attended-before attendees)
                            []
                            ["T-SHIRT SIZE"]
                            ~@(stats-field :tshirt-size attendees)
                            []
                            []
                            ["---- ACCEPTED ATTENDEES ----"]
                            []
                            ["AGE"]
                            ~@(stats-field :age accepted)
                            []
                            ["LANGUAGE PREFERENCE"]
                            ~@(stats-field :language-prefs accepted)
                            []
                            ["CLOJURE EXPERIENCE"]
                            ~@(stats-field :experience-clojure accepted)
                            []
                            ["ATTENDED BEFORE"]
                            ~@(stats-field :attended-before accepted)
                            []
                            ["T-SHIRT SIZE"]
                            ~@(stats-field :tshirt-size accepted)])))
