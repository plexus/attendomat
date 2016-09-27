function popup_attendee_selector() {
  return attendomat.core.popup_attendee_selector()
};

function attendee_list() {
  var a = attendomat.core.attendee_list();
  Logger.log(a);
  return a;
}

function onOpen(e) {
  attendomat.core.create_menu();
}
