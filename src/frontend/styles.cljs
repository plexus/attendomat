(ns frontend.styles)

(def state-colors
  {:waiting "#dcefe7"
   :invited "#A3CAF4"
   :comment "#C3DAF4"
   :accepted "#44C298"
   :cancelled "#FFAFC1"
   :rejected "#FFAFC1"})

(def styles
  [
   [:body {:font-family "sans-serif"}]
   [:input.text {:width "100%"
                 :margin-bottom "0.5em"}]

   (let [entry [:.entry {:border-bottom "1px solid #dddeee"
                         :padding "0.15em"}]
         entry-hover [:.entry:hover {:text-decoration "underline"
                                     :color "#1020aa"}]]

     [[:#attendee-list {:margin-bottom "1em"} entry entry-hover]
      [:#coaches-list {:margin-bottom "1em"} entry entry-hover]])

   [:#filter-checkboxes
    {:display "flex"
     :flex-wrap "wrap"
     :margin-bottom "0.5em"}
    [:.state-filter-checkbox {:width "50%"}]
    [:input {:display "block"
             :float "left"}]]

   [:#menu-panel
    [:ul {:padding "0"
          :margin "0"}]
    [:li {:border-bottom "1px solid #C3DAF4"
          :color "#4B76A6"
          :padding "0.75em"
          :text-align "center"
          :cursor "pointer"}]]

   [:.buttons {:display "flex"
               :justify-content "space-around"}]
   [:button {:border "1px solid #999"
             :border-radius "999em"
             :background-color "#99bbee"
             :color "#111111"
             :padding "0.4em 0.8em"
             :cursor "pointer"
             }]
   [:button:last-child {:margin-right "0"}]

   [:.back-arrow {:font-size "1.5em"}]
   [:.user-name {:text-decoration "underline"
                 :font-size "1.2em"}]
   [:.label {:display "inline-block"
             :padding "0.4em 0.75em"
             :border-radius "0.3em"}]

   [:.button {:font-family "Monospace"
              :background-color "#ddd"
              :border-color "#aaa"
              :border-radius "0.3em"
              :padding "0.3em"
              :cursor "pointer"}]

   [:.button--mail {:font-size "1.5em"
                    :padding "0.2em 0.75em"}]

   [:.top-bar
    {:display "flex"
     :margin-bottom "0.5em"
     :justify-content "space-between"}]

   [:.flex {:display "flex"
            :justify-content "space-between"}]

   [:.top-bar--right {:justify-content "flex-end"}]

   [:textarea {:width "100%"}]

   [:.email-entry
    {:font-size "0.9em"
     :border-bottom "1px solid #ccc"
     :padding "0.4em 0"}
    [:.subject-row {:display "flex"
                    :justify-content "space-between"
                    :margin-bottom "0.2em"}]
    [:.subject {:white-space "nowrap"
                :overflow "hidden"}
     [:a {:color "#000"
          :text-decoration "none"
          }]]
    [:.date {:color "#22f"
             :font-size "0.8em"
             :padding-left "0.5em"}]
    [:.preview {:color "#666"
                :font-size "0.85em"}]]

   [:.flag {:width "100%"
            :height "100%"
            :background-size "100% auto"
            :background-repeat "no-repeat"
            :background-position "left top"}]

   [:.flag-small {:width "18px" :height "12px"}]

   [:.flag-de {:background-image "url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiBoZWlnaHQ9IjYwMCIgdmlld0JveD0iMCAwIDUgMyI+DQo8cGF0aCBkPSJtMCwwaDV2M2gtNXoiLz4NCjxwYXRoIGZpbGw9IiNkMDAiIGQ9Im0wLDFoNXYyaC01eiIvPg0KPHBhdGggZmlsbD0iI2ZmY2UwMCIgZD0ibTAsMmg1djFoLTV6Ii8+DQo8L3N2Zz4NCg==')"}]

   [:.flag-uk {:background-image "url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB2aWV3Qm94PSIwIDAgNjAgMzAiIGhlaWdodD0iNjAwIj4NCjxkZWZzPg0KPGNsaXBQYXRoIGlkPSJ0Ij4NCjxwYXRoIGQ9Im0zMCwxNWgzMHYxNXp2MTVoLTMwemgtMzB2LTE1enYtMTVoMzB6Ii8+DQo8L2NsaXBQYXRoPg0KPC9kZWZzPg0KPHBhdGggZmlsbD0iIzAwMjQ3ZCIgZD0ibTAsMHYzMGg2MHYtMzB6Ii8+DQo8cGF0aCBzdHJva2U9IiNmZmYiIHN0cm9rZS13aWR0aD0iNiIgZD0ibTAsMGw2MCwzMG0wLTMwbC02MCwzMCIvPg0KPHBhdGggc3Ryb2tlPSIjY2YxNDJiIiBzdHJva2Utd2lkdGg9IjQiIGQ9Im0wLDBsNjAsMzBtMC0zMGwtNjAsMzAiIGNsaXAtcGF0aD0idXJsKCN0KSIvPg0KPHBhdGggc3Ryb2tlPSIjZmZmIiBzdHJva2Utd2lkdGg9IjEwIiBkPSJtMzAsMHYzMG0tMzAtMTVoNjAiLz4NCjxwYXRoIHN0cm9rZT0iI2NmMTQyYiIgc3Ryb2tlLXdpZHRoPSI2IiBkPSJtMzAsMHYzMG0tMzAtMTVoNjAiLz4NCjwvc3ZnPg0K')"}]

   [:.clojure-logo {:width "1em"
                    :height "1em"
                    :display "inline-block"
                    :background-size "100% 100%"
                    :background-repeat "no-repeat"
                    :background-position "left top"
                    :background-image "url('data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9Im5vIj8+CjxzdmcgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIiB4bWxuczpjYz0iaHR0cDovL2NyZWF0aXZlY29tbW9ucy5vcmcvbnMjIiB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiIHhtbG5zOnN2Zz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIGlkPSJzdmczMDk0IiB2aWV3Qm94PSItMC43NTggLTAuNzE1IDE2My4zODU4MiAxOTkuMjEwMDIiIGhlaWdodD0iNTAuMDAwMTM0IiB3aWR0aD0iNTAiIHZlcnNpb249IjEuMSI+CiAgPGRlZnMKICAgICBpZD0iZGVmczMwOTYiPgogICAgPGxpbmVhckdyYWRpZW50CiAgICAgICBpZD0ibDEiPgogICAgICA8c3RvcAogICAgICAgICBpZD0ic3RvcDMwOTkiCiAgICAgICAgIG9mZnNldD0iMCIKICAgICAgICAgc3R5bGU9InN0b3AtY29sb3I6IzgzODM4MztzdG9wLW9wYWNpdHk6MSIgLz4KICAgICAgPHN0b3AKICAgICAgICAgaWQ9InN0b3AzMTAxIgogICAgICAgICBvZmZzZXQ9IjEiCiAgICAgICAgIHN0eWxlPSJzdG9wLWNvbG9yOiM4MzgzODM7c3RvcC1vcGFjaXR5OjAiIC8+CiAgICA8L2xpbmVhckdyYWRpZW50PgogICAgPGxpbmVhckdyYWRpZW50CiAgICAgICBpZD0ibDIiPgogICAgICA8c3RvcAogICAgICAgICBpZD0ic3RvcDMxMDQiCiAgICAgICAgIG9mZnNldD0iMCIKICAgICAgICAgc3R5bGU9InN0b3AtY29sb3I6IzllOWU5ZTtzdG9wLW9wYWNpdHk6MC41ODgyMzUzMiIgLz4KICAgICAgPHN0b3AKICAgICAgICAgaWQ9InN0b3AzMTA2IgogICAgICAgICBvZmZzZXQ9IjEiCiAgICAgICAgIHN0eWxlPSJzdG9wLWNvbG9yOiM5ZTllOWU7c3RvcC1vcGFjaXR5OjAiIC8+CiAgICA8L2xpbmVhckdyYWRpZW50PgogICAgPGxpbmVhckdyYWRpZW50CiAgICAgICBpZD0ibDMiPgogICAgICA8c3RvcAogICAgICAgICBpZD0ic3RvcDMxMDkiCiAgICAgICAgIG9mZnNldD0iMCIKICAgICAgICAgc3R5bGU9InN0b3AtY29sb3I6I2ZmZmZmZjtzdG9wLW9wYWNpdHk6MC45MDE5NjA3OSIgLz4KICAgICAgPHN0b3AKICAgICAgICAgaWQ9InN0b3AzMTExIgogICAgICAgICBvZmZzZXQ9IjEiCiAgICAgICAgIHN0eWxlPSJzdG9wLWNvbG9yOiM4MzgzODM7c3RvcC1vcGFjaXR5OjEiIC8+CiAgICA8L2xpbmVhckdyYWRpZW50PgogICAgPGxpbmVhckdyYWRpZW50CiAgICAgICBpZD0ibDQiPgogICAgICA8c3RvcAogICAgICAgICBpZD0ic3RvcDMxMTQiCiAgICAgICAgIG9mZnNldD0iMCIKICAgICAgICAgc3R5bGU9InN0b3AtY29sb3I6I2JjYmNiYztzdG9wLW9wYWNpdHk6MSIgLz4KICAgICAgPHN0b3AKICAgICAgICAgaWQ9InN0b3AzMTE2IgogICAgICAgICBvZmZzZXQ9IjEiCiAgICAgICAgIHN0eWxlPSJzdG9wLWNvbG9yOiNmZmZmZmY7c3RvcC1vcGFjaXR5OjEiIC8+CiAgICA8L2xpbmVhckdyYWRpZW50PgogICAgPGxpbmVhckdyYWRpZW50CiAgICAgICBpZD0ibDUiPgogICAgICA8c3RvcAogICAgICAgICBpZD0ic3RvcDMxMTkiCiAgICAgICAgIG9mZnNldD0iMCIKICAgICAgICAgc3R5bGU9InN0b3AtY29sb3I6I2UyZTJlMjtzdG9wLW9wYWNpdHk6MSIgLz4KICAgICAgPHN0b3AKICAgICAgICAgaWQ9InN0b3AzMTIxIgogICAgICAgICBvZmZzZXQ9IjEiCiAgICAgICAgIHN0eWxlPSJzdG9wLWNvbG9yOiNmZmZmZmY7c3RvcC1vcGFjaXR5OjEiIC8+CiAgICA8L2xpbmVhckdyYWRpZW50PgogICAgPGxpbmVhckdyYWRpZW50CiAgICAgICBncmFkaWVudFRyYW5zZm9ybT0ibWF0cml4KDAuOTYwODc5LDAsMCwwLjk2MDg3OSwtMjMuMzc0NiwtMC4wNzg0ODQ0NykiCiAgICAgICBncmFkaWVudFVuaXRzPSJ1c2VyU3BhY2VPblVzZSIKICAgICAgIHhsaW5rOmhyZWY9IiNsNSIKICAgICAgIGlkPSJsMjEiCiAgICAgICB5Mj0iNTQxLjg5ODk5IgogICAgICAgeDI9IjgzOC4wODY5OCIKICAgICAgIHkxPSIxOTU0LjM1OCIKICAgICAgIHgxPSI4MzguMDg2OTgiIC8+CiAgICA8cmFkaWFsR3JhZGllbnQKICAgICAgIGdyYWRpZW50VHJhbnNmb3JtPSJtYXRyaXgoOC4yMzc3MzhlLTcsLTAuODg2NzIzLDEuODI2OSwxLjY5OTIxNWUtNiwtMTc3NS45MjUsMzMzMC4zOTQpIgogICAgICAgZ3JhZGllbnRVbml0cz0idXNlclNwYWNlT25Vc2UiCiAgICAgICB4bGluazpocmVmPSIjbDQiCiAgICAgICBpZD0icjIiCiAgICAgICBmeT0iMTgwNC43NDE1IgogICAgICAgZng9IjE2MDcuNDE5MiIKICAgICAgIHI9IjgwNi4zMDIiCiAgICAgICBjeT0iMTgwNC43NDE1IgogICAgICAgY3g9IjE2MDcuNDE5MiIgLz4KICAgIDxsaW5lYXJHcmFkaWVudAogICAgICAgZ3JhZGllbnRUcmFuc2Zvcm09Im1hdHJpeCgwLjk2MDg3OSwwLDAsMC45NjA4NzksLTIzLjM3NDYsLTAuMDc4NDg0NDcpIgogICAgICAgZ3JhZGllbnRVbml0cz0idXNlclNwYWNlT25Vc2UiCiAgICAgICB4bGluazpocmVmPSIjbDMiCiAgICAgICBpZD0ibDIyIgogICAgICAgeTI9IjI2NS4wMzE1NiIKICAgICAgIHgyPSIxMzg5LjI1NTciCiAgICAgICB5MT0iMzE1LjA3ODA2IgogICAgICAgeDE9IjEzNDAuMTM1MSIgLz4KICAgIDxyYWRpYWxHcmFkaWVudAogICAgICAgZ3JhZGllbnRUcmFuc2Zvcm09Im1hdHJpeCgtMS4wMDM1OTYsLTEuMDQ2Nzk3LDAuNTY4MTg2LC0wLjU0NDczNywyMjA3LjY4LDE5MTcuODk2KSIKICAgICAgIGdyYWRpZW50VW5pdHM9InVzZXJTcGFjZU9uVXNlIgogICAgICAgeGxpbms6aHJlZj0iI2wyIgogICAgICAgaWQ9InIzIgogICAgICAgZnk9IjQ4Mi4zNjc5NSIKICAgICAgIGZ4PSIxMTg4LjgwNTgiCiAgICAgICByPSIzNzkuNzYyODgiCiAgICAgICBjeT0iNDgyLjM2Nzk1IgogICAgICAgY3g9IjExODguODA1OCIgLz4KICAgIDxsaW5lYXJHcmFkaWVudAogICAgICAgZ3JhZGllbnRVbml0cz0idXNlclNwYWNlT25Vc2UiCiAgICAgICB4bGluazpocmVmPSIjbDEiCiAgICAgICBpZD0ibDIzIgogICAgICAgeTI9IjE5ODguMTcwNyIKICAgICAgIHgyPSI3NjguNDkwODQiCiAgICAgICB5MT0iMTg4Ny44MTA0IgogICAgICAgeDE9Ijc2Ny4xNTM5OSIgLz4KICA8L2RlZnM+CiAgPHBhdGgKICAgICBpZD0icGF0aDMxNDIiCiAgICAgc3R5bGU9ImRpc3BsYXk6bm9uZTtmaWxsOiNmZmZmMDA7ZmlsbC1ydWxlOmV2ZW5vZGQiCiAgICAgZD0ibSAxOTQ2Ljg4ODEsLTMwMzQuODI4OCAtNDUxLjI4NCwtNDUxLjI4NSBjIC0xNS45NDksLTE1Ljk0OCAtMzcuNTc5LC0yNC45MDcgLTYwLjEzMiwtMjQuOTA3IGwgLTEwNDcuOTMzOTUsMCBjIC0zMS4zMSwwIC01Ni42OTMsMjUuMzgyIC01Ni42OTMsNTYuNjkzIGwgMCwxODgzLjQ5MyBjIDAsMzEuMzExIDI1LjM4Miw1Ni42OTIgNTYuNjkzLDU2LjY5MiBsIDE1MjcuNTY0OTUsMCBjIDMxLjMxMSwwIDU2LjY5MiwtMjUuMzgyIDU2LjY5MiwtNTYuNjkyIGwgMCwtMTQwMy44NjIgYyAwLC0yMi41NTQgLTguOTU5LC00NC4xODQgLTI0LjkwNywtNjAuMTMyIHoiIC8+CiAgPHBhdGgKICAgICBkPSJtIDYwLjY3MTEyOCwxNDkuOTIyODcgMS4zNDMxNywwLjQ5NjE1IGMgNi4wNTg4LDIuMjI4MDQgMTIuNDIzNzcsMy4zNTg4MiAxOC45MTE2NywzLjM2Nzc2IDUuODk3ODgsLTAuMDA5IDExLjcyMjAxLC0wLjk1NDI0IDE3LjMxMzcyLC0yLjgxNTc3IGwgMy42MTgyOTIsLTEuMjA0NTMgLTIuODE4MjAyLC0yLjU2NTQ4IGMgLTEuMjcxNjYsLTEuMTY2NTQgLTIuMzI0MjksLTIuMjc5NDQgLTMuMjE4MjQsLTMuNDAzNTIgLTYuODI3NjEsLTguNzExMDMgLTEwLjY3MTYyLC0yMS42MzAwOCAtMTYuODczNDUsLTQzLjcwOTM0IGwgLTEuNjQwNDEsLTUuODM0OTA5IC0yLjU0MzMxLDUuNTAxOTM4IGMgLTAuOTk0NTMsMi4xNTQyODEgLTIuMDk0MSw0LjU3Njc0MSAtMy4yMzE2NSw3LjE2MDExMSAtMi45OTcsNi44MDAzMyAtOC4zMTM4MSwxOS40MjQzOSAtMTAuMTc3NzEsMjcuNTQ5OTEgLTAuNjUyNTcsMi44NDAzNyAtMC45OTQ1Myw2LjE1ODk1IC0wLjk5MDA2LDkuNTk1OTkgMCwxLjMxMTc5IDAuMDYwMiwyLjc2NDM4IDAuMTk0NDEsNC40MzU5NyBsIDAuMTExNzMsMS40MjU3NiB6IgogICAgIGlkPSJwYXRoMjItNiIKICAgICBzdHlsZT0iZmlsbDojOTFkYzQ3O3N0cm9rZS13aWR0aDowO3N0cm9rZS1taXRlcmxpbWl0OjQ7c3Ryb2tlLWRhc2hhcnJheTpub25lIiAvPgogIDxwYXRoCiAgICAgZD0ibSAyNi45NTU1MTgsMzUuMTg0NDYgMC40MDIyOCwwIGMgMTIuNzUyMywwLjA0MjUgMjEuNTcxMiw0LjQ0NzE0IDIzLjk3NTk0LDUuNzk5MTYgMC45ODU1OSwwLjU2NTM4IDEuOTM1NDIsMS4xODY2NSAyLjg3MTg0LDEuODIzNTUgbCAxLjAyMzU4LDAuNzAzOTQgMS4xMzUzMywtMC41MDA1OCBjIDcuNzkwODQsLTMuNDI4MSAxNi4wNTMyNCwtNS4xNjQ0OSAyNC41NjgxOSwtNS4xNjQ0OSAzMy42NjE5NjIsMC4wMDQgNjEuMDUyODIyLDI3LjM5MTI0NiA2MS4wNTcyODIsNjEuMDQ2NDc0IC0wLjAwNCwxNi40NDEwMDYgLTYuNDU2NiwzMS44Njk2NzYgLTE4LjE2NzQ0LDQzLjQzNDQ2NiBsIC0zLjMyOTk5LDMuMjg5NTQgNC42NTA4MSwwLjUxODQ0IGMgMy4wNzUyMSwwLjM0NjQgNi4wODMzOCwwLjUyMjk1IDguOTQ0MDUsMC41MjI5NSBsIDAuMzE3MzYsLTAuMDA1IGMgOS41MzE4MiwwIDIzLjAyNjEsLTEuODE0NjEgMzMuMzIwMDMsLTEwLjQ3NDI0IDMuODU1MTgsLTMuMjQ5MzIgNy4wMDYzOCwtNy4xNjY4MyA5LjQ5ODI5LC0xMS43NTY5OCAyLjE1NjY3LC04LjE0Nzg4IDMuMzE2NTgsLTE2LjcwNDcxIDMuMzE2NTgsLTI1LjUzMTk0MSAwLC01NS4wMDU5NzkgLTQ0LjU5MjgzLC05OS42MDQ3NDg5NyAtOTkuNjA0NzQyLC05OS42MDQ3NDg5NyAtMzUuNTQ4MjEsMCAtNjYuNzI3MjEsMTguNjM5OTk5OTcgLTg0LjM1MzgxOTcsNDYuNjYxNDM5OTcgQyA4LjE2NDUzNzgsMzYuOTc4NDIgMTkuNjU2MzU4LDM1LjE4MzkyIDI2Ljk1NTUxOCwzNS4xODM5MiBaIgogICAgIGlkPSJwYXRoMjQtNyIKICAgICBzdHlsZT0iZmlsbDojNTg4MWQ4O3N0cm9rZS13aWR0aDowO3N0cm9rZS1taXRlcmxpbWl0OjQ7c3Ryb2tlLWRhc2hhcnJheTpub25lIiAvPgogIDxwYXRoCiAgICAgZD0ibSAxMzUuMjQ5NTMsMTU3LjU0MTEyIGMgLTIuODk0MTksMCAtNS42NDUzNSwtMC4xNTY0NSAtOC4xNzUyNCwtMC40NjcwOSAtNy43MzQ5NywtMC45NzQzNSAtMTIuOTA4NzUsLTIuMDQ3MDMgLTE3LjI4OTE0LC0zLjU5MzQ3IGwgLTAuODkzOTYsLTAuMzE1MDggLTAuODQ3MDIsMC40MjAxMSBjIC04LjQ4MTQyMiw0LjIxMDI2IC0xNy42MDIwMjIsNi4zNDg5IC0yNy4xMTE0OTIsNi4zNDg5IC0zMy42NTUyNywwIC02MS4wNDM4OSwtMjcuMzg2NzcgLTYxLjA0ODM2LC02MS4wNDIwMDYgMCwtMTcuNTU4MzcxIDcuNTg3NDYsLTM0LjI4MzE5NCAyMC44MTU4LC00NS44ODU5NzQgbCAzLjI0MjgzLC0yLjg0MjU3IC00LjE5MjY2LC0xLjAxMDEgYyAtMy44NjYzNywtMC45MzE4OSAtNy43MDgxNSwtMS40MDc4OSAtMTEuNDA2OSwtMS40MDc4OSAtMjAuMDAwMDYwMiwwLjE4NTQ4IC0zNS44MjUzMzk3LDEwLjk4Mzc2MiAtNDQuNjQyLDI5LjU3NjgyMyAtMS41MzUzNyw2Ljk0NzgxNiAtMi4zNzEyMiwxNC4xNTkzMzcgLTIuMzcxMjIsMjEuNTY5NzQxIDAsNTUuMDA4MjA2IDQ0LjU5NzMsOTkuNjAyNTA2IDk5LjYwNDc0LDk5LjYwMjUwNiAzNC45MTM1MTIsMCA2NS42MDk3NzIsLTE3Ljk3NjI4IDgzLjM5MjgyMiwtNDUuMTYxOTMgLTEwLjMzMTkyLDIuODExMzEgLTIwLjQwNjgyLDQuMTc4OTcgLTI5LjA3ODIsNC4yMDgwMyB6IgogICAgIGlkPSJwYXRoMjYtNSIKICAgICBzdHlsZT0iZmlsbDojNjNiMTMyO3N0cm9rZS13aWR0aDowO3N0cm9rZS1taXRlcmxpbWl0OjQ7c3Ryb2tlLWRhc2hhcnJheTpub25lIiAvPgogIDxwYXRoCiAgICAgZD0ibSAxMDcuMTk5MzgsMTQyLjAzODcgYyAwLjcyMTg0LDAuMzY0MjUgMi4zMDQxOCwwLjkxMTc3IDQuMjMyODksMS40NTI1OSBsIDEuMDUyNjMsMC4yOTcyMyAwLjg3NjA4LC0wLjY0MzU2IGMgMTQuMDQ0MDgsLTEwLjMwNDQgMjIuNDUxNzQsLTI2Ljg1MDQ0IDIyLjQ4MDc5LC00NC4yNTAxNSAtMC4wNjAyLC0zMC4yMTU5NTkgLTI0LjY4ODg2LC01NC44NDI4MyAtNTQuOTA2ODYyLC01NC44OTg3IGwgLTAuMDA0LDAgLTAuMDA0LDAgYyAtNS44NTU0MiwwLjAwOSAtMTEuNjU0OTcsMC45NDk3NyAtMTcuMjM1NSwyLjc5NzkgbCAtMy4yMDAzNywxLjA2MTUgMi4yMjM3MiwyLjUzNDIgYyAxMC4zNDc1NiwxMS43ODYwMzggMTUuMzI2OSwyOC42MDAyNDMgMjAuMzMwODMsNDcuNzUxOTk3IGwgMC4wMTM1LDAuMDQ2NjggMC4wNjAyLDAuMTg5OTUzIGMgMC41NDUzNSwxLjc1ODczIDIuMDU2MSw2LjQ4Mjk5IDQuMzIyMjgsMTIuMzI0NjEgMi4wMjI1OCw1LjI0MDQ3IDYuMTM3MDIsMTUuMTI0NzQgMTAuOTQ4NzQsMjIuMzcyMDEgMi45MTY1NTIsNC40ODA2NiA2LjIxMDc4Miw3LjgzOTQ3IDguODA5OTYyLDguOTYzNTUgeiIKICAgICBpZD0icGF0aDI4LTMiCiAgICAgc3R5bGU9ImZpbGw6IzkwYjRmZTtzdHJva2Utd2lkdGg6MDtzdHJva2UtbWl0ZXJsaW1pdDo0O3N0cm9rZS1kYXNoYXJyYXk6bm9uZSIgLz4KICA8cGF0aAogICAgIGQ9Im0gNzUuNTQ4ODA4LDkxLjk4OTM5NiAwLjQyMDE0LC0wLjg1ODE0NSAtMC4zMjg1MSwtMC44OTM4OTMgYyAtMC43Mjg1NCwtMi4wMDQ1NjMgLTEuNTYyMTksLTQuMjAxMzIyIC0yLjQ4NzQzLC02LjUwMzEwMiAtMS41OTM0OCwtMy45ODkwMTYgLTcuMjM4ODIsLTE3LjQ3NTY5NiAtMTMuMDM4MzcsLTIzLjQxNzg3NiAtMi4yMjM3MiwtMi4zNDQyNSAtNS4wMzUyMSwtNC41MDc0OCAtOC4zMzM5MiwtNi40MDkyNCBsIC0xLjIzNTg5LC0wLjcxMjg5IC0xLjE2NjYyLDAuODI0NjIgYyAtMTQuNTg0OTEsMTAuMjc3NTg5IC0yMy4zMTIxNywyNy4wNDkzMzkgLTIzLjM0NTcsNDQuODY0NzA3IDAuMDI5MSwxNy41MTgxNTMgOC41MjE2NSwzNC4xMjY3NjMgMjIuNzEwOTksNDQuNDI2NjkzIGwgMi43MTA5MywxLjk2ODgxIDAuNzc3NzQsLTMuMjQ5MzIgYyAyLjE4MTI2LC05LjA3NzUzIDYuNjA0MTEsLTE3LjU1ODM4IDEzLjI5NzYyLC0zMC4zOTkyMSAyLjk0NTU5LC01LjY0NzIgNi4yODg5OSwtMTIuMDU0MTk5IDEwLjAxOTAyLC0xOS42NDExNTQgeiIKICAgICBpZD0icGF0aDMwLTUiCiAgICAgc3R5bGU9ImZpbGw6IzkxZGM0NztzdHJva2Utd2lkdGg6MDtzdHJva2UtbWl0ZXJsaW1pdDo0O3N0cm9rZS1kYXNoYXJyYXk6bm9uZSIgLz4KPC9zdmc+Cg==')"}]

   (mapv (fn [[state color]]
           [(keyword (str ".state-" (name state))) {:background-color color}])
         state-colors)])
