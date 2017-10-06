var sidebarFile = "sidebar.html";

function onOpen(e) {
  return backend.core.on_open_hook(e);
}

function onEdit(e) {
  return backend.core.on_edit_hook(e);
}

function backendCall(name, args) {
  return backend.core.backend_call(name, args);
}
