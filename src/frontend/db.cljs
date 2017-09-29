(ns frontend.db)

(def default-db
  {:state :attendee-list
   :inspector-state nil
   :previous-state nil
   :menu-open? false
   :attendees {}
   :coaches {}
   :groups {}
   :coaches-spreadsheet nil
   :filter-value ""
   :filter-coach ""
   :show-states #{:waiting :invited :accepted :cancelled}
   :emails {}
   :selected-attendee nil
   :selected-coach nil})
