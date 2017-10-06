(ns backend.summarize
  (:require [attendomat.attendees :refer [filter-state]]
            [attendomat.helpers :refer [present?]]
            [backend.sheets :as sh]
            [backend.event-sourcing :as es]))

(defn summarize-field [field attendees]
  (->> attendees
       (filter (comp present? field))
       (map (juxt :first-name :last-name field))))

(defn stats-field [field attendees]
  (map vec (frequencies (map field attendees))))

(defn summarize-group [{:keys [attendees coaches name index] :as group}]
  (let [re-find (fn [re s]
                  (when (string? s)
                    (re-find re s)))
        icons #(str
                (when (re-find #"German|Deutsch|Both" (:language-prefs %)) "🇩🇪")
                (when (re-find #"English|Both" (:language-prefs %)) "🇬🇧")
                (when (= "Coding beginners (ppl who never code before)" (:coaching-prefs %)) "🌱")
                (when (and (re-find #"No" (:experience-clojure %))
                           (re-find #"(?i)^\s*$|^no|^nein" (:experience-other %))) "🌱"))]
    `[[~(str (inc index) ". " name)]
      ~@(map (fn [{:keys [first-name last-name experience-clojure other-langs] :as coach}]
               [(str "Coach: " first-name " " last-name)
                (icons coach)
                experience-clojure
                other-langs])
             coaches)
      ~@(map (fn [{:keys [first-name last-name experience-clojure experience-other] :as attendee}]
               [(str first-name " " last-name)
                (icons attendee)
                experience-clojure
                experience-other])
             attendees)]))

(defn ^:export summarize []
  (let [data (es/app-data)
        attendees (vals (:attendees data))
        coaches (filter-state :accepted (vals (:coaches data)))
        groups (sort-by :index (vals (:groups data)))
        accepted (filter-state :accepted attendees)
        cols [:first-name :last-name :email :language-prefs
              :age :gender :attended-before :experience-other
              :experience-clojure :heard-of-us :comment]]

    (doseq [type [:waiting :invited :accepted :cancelled]]
      (sh/update-sheet-data (name type)
                            (into [(map name cols)]
                                  (map (apply juxt cols)
                                       (filter-state type attendees)))))

    (sh/update-sheet-data "Coaches"
                          `[["First name" "Last name" "Email" "Phone" "Language prefs" "Gender" "Experience Clojure" "Other langs"]
                            ~@(map (juxt :first-name :last-name :email :phone :language-prefs :gender :experience-clojure :other-langs) coaches)])

    (sh/update-sheet-data "Food/Assistance/Childcare"
                          `[["--- THIS SHEET ONLY CONTAINS INFORMATION FROM SELECTED ATTENDEES ---"]
                            []
                            ["FOOD PREFERENCES (includes coaches)"]
                            ~@(summarize-field :food-prefs (concat accepted coaches))
                            []
                            ["SPECIAL ASSISTANCE"]
                            ~@(summarize-field :assistance accepted)
                            []
                            ["CHILDCARE"]
                            ~@(summarize-field :childcare accepted)])

    (sh/update-sheet-data "Groups"
                          (into [["Name" "Icons" "Experience Clojure" "Experience other"]]
                                (reduce (fn [rows group]
                                          (conj (into rows (summarize-group group)) []))
                                        [] groups)))

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
                            ["T-SHIRT SIZE (includes coaches)"]
                            ~@(stats-field :tshirt-size (concat accepted coaches))])))
