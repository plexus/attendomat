(ns frontend.db)

(def default-db
  {:state :attendee-list
   :inspector-state nil
   :previous-state nil
   :menu-open? false
   :attendees []
   :coaches []
   :coaches-spreadsheet nil
   :filter-value ""
   :show-states #{:waiting :invited :accepted :cancelled}
   :emails {}
   :selected-attendee nil
   :selected-coach nil})
