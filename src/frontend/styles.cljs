(ns frontend.styles)

(def state-colors
  {:waiting "#dcefe7"
   :invited "#7DBBFF"
   :accepted "#9CE5D5"
   :cancelled "#FFAFC1"})

(def styles
  [
   [:body {:font-family "sans-serif"}]
   [:input.text {:width "100%"
                 :margin-bottom "0.5em"}]

   [:#attendee-list {:margin-bottom "1em"}
    [:.entry {:border-bottom "1px solid #eeeeee"
              :padding "0.1em"}]
    [:.entry:hover {:text-decoration "underline"
                    :color "#1020aa"}]]

   [:#filter-checkboxes
    {:display "flex"
     :flex-wrap "wrap"
     :margin-bottom "0.5em"}
    [:label {:width "50%"}]
    [:.state-filter-checkbox {:padding-top "0.1em"
                              :padding-bottom "0.2em"}]]

   [:#action-buttons {:margin-bottom "1em"}]
   [:.buttons {:display "flex"}]
   [:button {:border "1px solid #222222"
             :border-radius "0.5em"
             :background-color "#99bbee"
             :color "#111111"
             :padding "0.5em"
             :margin-right "1em"
             :flex "1 1 0"
             :cursor "pointer"}]
   [:button:last-child {:margin-right "0"}]

   [:.back-arrow {:font-size "1.5em"}]
   [:.user-name {:text-decoration "underline"
                 :font-size "1.2em"}]
   [:.label {:display "inline-block"
             :padding "0.4em 0.75em"
             :float "right"
             :border-radius "0.3em"}]

   [:.top-bar
    {:display "flex"
     :margin-bottom "0.5em"
     :justify-content "space-between"}

    [:.button {:font-family "Monospace"
               :background-color "#ddd"
               :border-color "#aaa"
               :border-radius "0.2em"
               :padding "0.3em"
               :cursor "pointer"}]]

   [:.top-bar--right {:justify-content "flex-end"}]

   [:#inspector
    [:textarea {:width "100%"}]]

   (mapv (fn [[state color]]
           [(keyword (str ".state-" (name state))) {:background-color color}])
         state-colors)])
