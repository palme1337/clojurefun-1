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
	 (let [new-state (fn [] (merge @checkbox-state {
				 "Tree Owner" false,
				 "Approval params" true
				 }))]
	  [:div
    [:h1 "Create a new configuration Request"]
	  (map checkbox (seq @checkbox-state) )
	  [submit-button "Click me lol"]
	  [:button 
	  {:on-click #(swap! checkbox-state new-state)}
	  "Test the state here"]
	  ]
	 )
	)


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
