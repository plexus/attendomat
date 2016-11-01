(ns frontend.views
  (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [frontend.styles :as styles]
            [garden.core :refer [css]]))

(defn main-panel []
  (let [atts (subscribe [:attendees])]
    (fn []
      [:div
       [:style {:type "text/css"} (css styles/styles)]
       #_[:button {:on-click #(dispatch [:fetch-attendees])} "Fetch"]

       [:input {:type "text"}]

       [:div
        (for [a @atts]
          (let [name (str (:first-name a) " " (:last-name a))]
            [:div {:key name} name]))
        #_[:pre (prn-str @atts)]]])))
