# Attendomat

An extension to Google Sheets to help with the managing of attendees for
ClojureBridge Berlin.

## Build

```
lein cljsbuild once main
```

## Install

Go to the sheet that contains the form submissions. Choose Tools > Script
Editor. Call the Script "Attendomat".

Copy the contents of `export/Code.gs` into the script editor.

Also create `sidebar-figwheel.html`. You can find the contents under
`export/sidebar-figwheel.html`. This file is only used for development, see
"Deployment" for how to push the final version.

## Overview

Attendomat consists of a frontend and a backend part. The backend is "Google
Apps Script", it is executed on Google's servers.

This code can manipulate the spreadsheet data, and it can respond to certain
"triggers" (hooks), like `onOpen` and `onEdit`.

In the `onOpen` hook, it creates a sidebar for the sheet, so whenever you open
the spreadsheet, a "ClojureBridge" sidebar appears. We can load whatever HTML we
want in that sidebar, it just gets loaded as an iframe, so we stick our own UI
in there, which is a reagent/re-frame app. In dev mode we load Figwheel in there
so this "front end" automatically reloads changes.

This "frontend" does not have direct access to the spreadsheet. Even though the
spreadsheet UI and our "app" running in the sidebar are in the same browser
window, they can't talk to each other directly.

Instead Google provides a way for this "frontend" to invoke functions on the
"backend", so we can query the server to retrieve data from the spreadsheet, or
send commands to update it. The visible spreadsheet will update accordingly.

## Building the backend

If you look at `project.clj` you'll find three Clojurescript builds. The one
called "main" is for the backend. It creates `export/Code.gs`, which is the file
you copy into the Google Apps Script editor. To build it, invoke

```
lein cljsbuild once main
```

If you have some kind of command line tool to interact with the clipboard, you
can create a oneliner to build + copy this code, so you can then simply paste it
into the editor.

e.g. using `xclip` on Linux.

```
lein cljsbuild once main ; cat export/Code.gs | xclip -selection clipboard
```

## Developing the frontend

By default the sidebar will load `sidebar-figwheel.html`, which will try to
connect to Figwheel on its default port (3449), so simply start Figwheel, from
the command line or from your editor.

```
lein figwheel
```

To get Figwheel to work correctly inside Google sheets you need to disable some
browser security checks. The only way I've found that works is to use Google
Chrome (*not* Chromium), and to invoke it with
`--allow-running-insecure-content`.

(It's also recommended you use Chrome canary or unstable to make sure
Cljs-Devtools works properly, but that's optional).

```
google-chrome-unstable --allow-running-insecure-content
```

Now reload the sheet, and assuming you have the right `Code.gs` and
`sidebar-figwheel.html` set up, the sidebar should pop up with a list of
attendees.

## Deploying

Once you're ready developing and you want people to start using the app, you'll
have to deploy it in a way that doesn't rely on Figwheel to be running. This way
it's also a bit less picky about browsers, so that's also good.

Run the production build for the GUI,

```
lein cljsbuild once gui-prod
```

This creates `export/gui.js`. You need to put this file online somewhere so it's
publicly accessible, and served with the right content type
(application/javascript). One easy way to do this is with Github's Gist.

Create the gist with the file, the find the username + gist ID in the URL, and
fill them in in `exports/sidebar.html`

```html
<script src="https://rawgit.com/<username>/<gist-id>/raw/gui.js"></script>
```

Now in the Google App Script editor, create `sidebar.html`, and copy over
`exports/sidebar.html`.

In `Code.gs`, you'll find this line near the top

``` javascript
var sidebarFile = "sidebar-figwheel.html";
```

Change it to point to `sidebar.html` instead.


## License ##

Copyright © 2016 Arne Brasseur

Distributed under the Mozilla Public License 2.0
