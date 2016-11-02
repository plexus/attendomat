(ns frontend.db)

(def default-db
  {:state :attendee-list
   :attendees []
   :filter-value ""
   :show-states #{:waiting :invited :accepted :cancelled}})
