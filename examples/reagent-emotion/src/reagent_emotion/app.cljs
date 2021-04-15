(ns reagent-emotion.app
  (:require
   [shadow.resource :as resource]
   [reagent.dom :as rdom]
   [reagent.core :as r]
   [emotion.core :as emotion]
   [reagent-emotion.components.button.core :refer [Button]]
   [reagent-emotion.components.mui-button.core :refer [MuiButton]]))

(def reset-styles (resource/inline "ress/ress.css"))

(def root-el (js/document.getElementById "root"))

(def Global (r/adapt-react-class emotion/Global))

(defn render! []
  (rdom/render
   [:<>
    [Global reset-styles]
    [:div
     [Button
      {:class-name "primary-global-class"
       :color      "primary"
       :on-click   #(js/alert "Primary")}
      "Primary"]
     " "
     [Button
      {:class-name "secondary-global-class"
       :color      "secondary"
       :on-click   #(js/alert "Secondary")}
      "Secondary"]]
    [:br]
    [:div
     [MuiButton
      {:className "primary-global-class"
       :color     "primary"
       :variant   "contained"
       :onClick   #(js/alert "Primary MUI")}
      "Primary MUI"]
     " "
     [MuiButton
      {:className "secondary-global-class"
       :color     "secondary"
       :variant   "contained"
       :onClick   #(js/alert "Secondary MUI")}
      "Secondary MUI"]]]
   root-el))

(defn init []
  (render!))
