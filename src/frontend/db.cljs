(ns frontend.db)

(def default-db
  {:state :attendee-list
   :inspector-state nil
   :previous-state nil
   :menu-open? false
   :attendees []
   :filter-value ""
   :show-states #{:waiting :invited :accepted :cancelled}
   :emails {}})
