var sidebarFile = "sidebar-figwheel.html";

function onOpen(e) {
  return attendomat.core.on_open_hook(e);
}

function onEdit(e) {
  return attendomat.core.on_edit_hook(e);
}

function attendeeData() {
  return attendomat.core.attendee_data();
}
