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

## License

Copyright © 2016 Arne Brasseur

Distributed under the Mozilla Public License 2.0
