var sidebarFile = "sidebar-figwheel.html";

function onOpen(e) {
  return backend.core.on_open_hook(e);
}

function onEdit(e) {
  return backend.core.on_edit_hook(e);
}

function attendeeData() {
  return backend.core.attendee_data();
}
