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
browser security checks.

### Chrome

Use Google Chrome (*not* Chromium), with the `--allow-running-insecure-content`

(It's also recommended you use Chrome canary or unstable to make sure
Cljs-Devtools works properly, but that's optional).

```
google-chrome-unstable --allow-running-insecure-content
```

Now reload the sheet, and assuming you have the right `Code.gs` and
`sidebar-figwheel.html` set up, the sidebar should pop up with the Attendomat GUI.

### Firefox

Firefox is a bit more picky when it comes to doing (in)secure things. You can
disable protection for the current page, and this will make the sidebar load,
but Figwheel's websocket will fail to connect, and so you're back to square one.

What I have found to work is using `ngrok`. This tool will tunnel all traffic
from a public host like `2dc5485f.ngrok.io` to a port on your computer, and it
accepts both `http` and `https` traffic. This way you can make sure the frontend
and Figwheel's websocket are all loaded over HTTPS, making the browser happy.
This does involve a few steps.

1. install [ngrok](http://ngrok.io/)
2. in a seperate terminal run `ngrok http 3449`
3. make note of the domain name you've been assigned (e.g. `your-domain.ngrok.io`)
4. In `sidebar-figwheel.html` (in the Google App Script editor), change the Figwheel URL from `http://localhost:3449/js/gui-dev.js` to `https://your-domain.ngrok.io/js/gui-dev.js`
5. in `project.clj`, change the `:asset-path` to `https://your-domain.ngrok.io/js/gui-dev`
6. in `project.clj`, set `:websocket-url` to `wss://your-domain.ngrok.io/figwheel-ws`

Now (re)start figwheel, reload the page, and you should see the terminal running Figwheel drop into a ClojureScript REPL. That's when you know all is good.

## Granting Permission

Normally the names of attendees should show up in the sidebar, but chances are
they won't, because the code doesn't yet have access to the data.

Go to the script editor again (Tools > Script Editor), from the "Select a
Function" dropdown select "onOpen", then press the "Play" button (a right
pointing triangle). A dialog will pop up where you can grant permission.

## Updating for a new workshop

When you follow these instructions, a new sheet will pop up called "Errors", which will look like this

```
No field mapping for "First Name"
No field mapping for "Last Name"
No field mapping for "Do you plan to travel to Berlin from outside Berlin/Brandenburg for the workshop?"
```

This is because the form has been updated since the previous workshop, and fields have been renamed or added. The first thing to do is go into `src/attendomat/attendees.cljs`, and add these fields. In case of a rename just add them, don't remove the old ones. It doesn't hurt, and if people change their mind again it continues to work.

``` clojure
(def fields
  {"First Name" :first-name
   "Last Name" :last-name
   ,,,})
```

This assures they get read correctly and added to the attendee data under the given key.

Specific fields like food preferences or childcare are also displayed when looking at the attendee details. For this you go to `src/frontend/views.cljs` and update `selected-attendee-panel`. There's a big destructuring form at the top, add the necessary keys there, then add whatever hiccup you want to the view.


``` clojure
(defn selected-attendee-panel []
  (let [attendee (subscribe [:selected-attendee])]
    (fn []
      (let [{:keys [first-name last-name email state age gender
                    experience-other experience-clojure language-prefs
                    food-prefs assistance childcare heard-of-us comment
                    history travel]} @attendee]
        [:div#selected-attendee
         ,,,
         (if (present? travel)
           [:div [:h3 "Travel"] [:p travel]])
         ,,,
         ]))))
```

To also display this data in the summaries, update `src/backend/summarize.cljs`. You can follow the pattern given there, for instance this:

```
(sh/update-sheet-data "Feedback"
                          `[["HOW DID YOU HEAR FROM US"]
                            ~@(summarize-field :heard-of-us attendees)
                            []
                            ["COMMENTS"]
                            ~@(summarize-field :comment attendees)
                            []])
```

Creates a new sheet called "Feedback", which contains a summary of the `:heard-of-us` and `:comment` fields, for all selected attendees.

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
