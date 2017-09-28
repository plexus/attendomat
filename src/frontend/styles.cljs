(ns frontend.styles)

(def state-colors
  {:waiting "#dcefe7"
   :invited "#A3CAF4"
   :comment "#C3DAF4"
   :accepted "#44C298"
   :cancelled "#FFAFC1"
   :rejected "#FFAFC1"})

(def styles
  [
   [:body {:font-family "sans-serif"}]
   [:input.text {:width "100%"
                 :margin-bottom "0.5em"}]

   (let [entry [:.entry {:border-bottom "1px solid #dddeee"
                         :padding "0.15em"}]
         entry-hover [:.entry:hover {:text-decoration "underline"
                                     :color "#1020aa"}]]

     [[:#attendee-list {:margin-bottom "1em"} entry entry-hover]
      [:#coaches-list {:margin-bottom "1em"} entry entry-hover]])

   [:#filter-checkboxes
    {:display "flex"
     :flex-wrap "wrap"
     :margin-bottom "0.5em"}
    [:.state-filter-checkbox {:width "50%"}]
    [:input {:display "block"
             :float "left"}]]

   [:#menu-panel
    [:ul {:padding "0"
          :margin "0"}]
    [:li {:border-bottom "1px solid #C3DAF4"
          :color "#4B76A6"
          :padding "0.75em"
          :text-align "center"
          :cursor "pointer"}]]

   [:.buttons {:display "flex"
               :justify-content "space-around"}]
   [:button {:border "1px solid #999"
             :border-radius "999em"
             :background-color "#99bbee"
             :color "#111111"
             :padding "0.4em 0.8em"
             :cursor "pointer"
             }]
   [:button:last-child {:margin-right "0"}]

   [:.back-arrow {:font-size "1.5em"}]
   [:.user-name {:text-decoration "underline"
                 :font-size "1.2em"}]
   [:.label {:display "inline-block"
             :padding "0.4em 0.75em"
             :border-radius "0.3em"}]

   [:.button {:font-family "Monospace"
              :background-color "#ddd"
              :border-color "#aaa"
              :border-radius "0.3em"
              :padding "0.3em"
              :cursor "pointer"}]

   [:.button--mail {:font-size "1.5em"
                    :padding "0.2em 0.75em"}]

   [:.top-bar
    {:display "flex"
     :margin-bottom "0.5em"
     :justify-content "space-between"}]

   [:.flex {:display "flex"
            :justify-content "space-between"}]

   [:.top-bar--right {:justify-content "flex-end"}]

   [:textarea {:width "100%"}]

   [:.email-entry
    {:font-size "0.9em"
     :border-bottom "1px solid #ccc"
     :padding "0.4em 0"}
    [:.subject-row {:display "flex"
                    :justify-content "space-between"
                    :margin-bottom "0.2em"}]
    [:.subject {:white-space "nowrap"
                :overflow "hidden"}
     [:a {:color "#000"
          :text-decoration "none"
          }]]
    [:.date {:color "#22f"
             :font-size "0.8em"
             :padding-left "0.5em"}]
    [:.preview {:color "#666"
                :font-size "0.85em"}]]

   (mapv (fn [[state color]]
           [(keyword (str ".state-" (name state))) {:background-color color}])
         state-colors)])
