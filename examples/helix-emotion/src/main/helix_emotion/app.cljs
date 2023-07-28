(ns helix-emotion.app
  (:require
   [shadow.resource :as resource]
   ["react-dom/client" :refer [createRoot]]
   [helix.core :refer [$ <>]]
   [helix.experimental.refresh :as refresh]
   [emotion.core :refer [Global]]
   [helix-emotion.components.button.core :refer [button]]
   [helix-emotion.components.mui-button.core :refer [mui-button]]))

(def reset-styles (resource/inline "ress/ress.css"))

(refresh/inject-hook!)

(defn ^:dev/after-load after-load []
  (refresh/refresh!))

(def root (-> (js/document.getElementById "root")
                 createRoot))

(defn render! []
  (.render
   root
   (<>
    ($ Global reset-styles)
    ($ :div
       ($ button
          {:class-name "primary-global-class"
           :color      "primary"
           :on-click   #(js/alert "Primary")}
          "Primary")
       " "
       ($ button
          {:class-name "secondary-global-class"
           :color      "secondary"
           :on-click   #(js/alert "Secondary")}
          "Secondary"))
    ($ :br)
    ($ :div
       ($ mui-button
          {:className "primary-global-class"
           :color     "primary"
           :variant   "contained"
           :onClick   #(js/alert "Primary MUI")}
          "Primary MUI")
       " "
       ($ mui-button
          {:className "secondary-global-class"
           :color     "secondary"
           :variant   "contained"
           :onClick   #(js/alert "Secondary MUI")}
          "Secondary MUI")))))

(defn unmount! []
  (.unmount root))

(defn init []
  (render!))
