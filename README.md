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

For each HTML file under export, create a HTML file in the script editor (File >
New > HTML file) with the same name and contents.

## Overview

Attendomat consists of a frontend and a backend part. The backend is "Google
Apps Script", it is executed on Google's servers.

This code can manipulate the spreadsheet data, and it can respond to certain
"triggers" (hooks), like `onOpen` and `onEdit`.

In the `onOpen` hook, we create a sidebar for the sheet. We can load whatever
HTML we want in that sidebar, it just gets loaded as an iframe, so we stick our
own UI in there, which is a reagent/re-frame app.

This "frontend" can call the "backend" with `google.script.run` to call
functions on the backend, and can access the spreadsheet data like that.

In development mode, we stick an iframe in the sidebar which connects to
Figwheel running locally, so you get automatic reloading of the UI in the
sidebar.

## Getting started

Building the frontend, this creates `export/gui.js`

```
lein cljsbuild once gui-prod
```

Building the backend, this creates `export/Code.gs`

```
lein cljsbuild once main
```

Go to the google sheet, and go to Tools > Script Editor. Copy the code of
`export/Code.gs` into the editor, save it, and create a HTML file for each HTML
file in `export`.

Each time you change the backend code, you need to compile it again, then
copy-paste it into `script.google.com`. To speed this up a bit use a oneliner like this to compile+copy to clipboard in one go

(Assuming Linux and having `xclip` available)

```
$ lein cljsbuild once main ; cat export/Code.gs | xclip -selection clipboard
```

Start Figwheel (`lein figwheel`), and load the page, it will run the `onOpen`
hook, this will open the sidebar. The sidebar will try to load Figwheel, but
because it's http inside https it'll be blocked.

~In Firefox at least you can click on the little lock left of the address bar, and choose "Disable Protection For Now".~

Scratch that, doesn't work in Firefox because it still refuses the Websocket
connection for Figwheel. Instead use Google Chrome (not Chromium, I tried it),
and start it with `--allow-running-insecure-content`.


## License ##

Copyright © 2016 Arne Brasseur

Distributed under the Mozilla Public License 2.0
