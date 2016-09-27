function popup_attendee_selector() {
  return sheets.core.popup_attendee_selector()
};

function attendee_list() {
  var a = sheets.core.attendee_list();
  Logger.log(a);
  return a;
}

function onOpen(e) {
  sheets.core.create_menu();
}
