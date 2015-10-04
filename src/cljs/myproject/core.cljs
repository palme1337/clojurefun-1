(ns myproject.core
  (:require [reagent.core :as reagent :refer [atom]]
    [reagent.session :as session]
    [secretary.core :as secretary :include-macros true]
    [goog.events :as events]
    [goog.history.EventType :as EventType])
  (:import goog.History))

;; -------------------------
;; Views

(def checkbox-state
  (reagent/atom 
  {
    "Tree Owner desu" true,
    "Approval params desuuu" true,
    "Herb please" false
  })
  )

(defn checkbox [name-to-state]
  (let [checkbox-name (first name-to-state)
    checked? (second name-to-state)
    on-click-handler (fn [] (assoc @checkbox-state checkbox-name (not checked?)))
    ]
    [:div {:key name-to-state}
    [:input {:id checkbox-name,
      :type "checkbox",
      :checked checked?,
      :on-click #(swap! checkbox-state on-click-handler)
    }
    ]
    [:label {:for checkbox-name} checkbox-name]
    ]
    )
  )


(defn submit-button [value]
  [:button {     
    :disabled (every? false? (vals @checkbox-state))
    } value
  ]
)

(defn home-page []
  (let [weep (reagent/atom {
    "Tree Owner desu" false,
    "Approval params desuuu" true
  })
  __ (print "shutup")] 
    [:div
     (map checkbox (seq @checkbox-state) )
     [submit-button "Click me lol"]
     [:button 
        "im text in a streing"]
     ]
     )
)


; (defn home-page []
;   (let [checkbox-text ["Tree Owner" "Approval Params"]]
;   [:div [:h2 "Welcome to myproject"]
;   (map checkbox-names checkbox-text)
;   [:div [:a {:href "#/about"} "go to about page"]]
;   [:button {:disabled false} "If you chew Big Red, then fuck you."]]
; ))

; (defn about-page []
;   [:div [:h2 "About myproject"]
;   [:div [:a {:href "#/"} "go to the home page"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

; (secretary/defroute "/about" []
;   (session/put! :current-page #'about-page))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))
