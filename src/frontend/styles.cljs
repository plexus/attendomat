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
                 :margin-bottom "1em"}]

   [:#attendee-list {:margin-bottom "1em"}
    [:.entry {:border-bottom "1px solid #eeeeee"
              :padding "0.1em"}]
    [:.entry:hover {:text-decoration "underline"
                    :color "#1020aa"}]]

   [:#filter-checkboxes
    {:display "flex"
     :flex-wrap "wrap"
     :margin-bottom "0.5em"}
    [:label {:width "50%"}]]

   (mapv (fn [[state color]]
           [(keyword (str ".state-" (name state))) {:background-color color}])
         state-colors)])
