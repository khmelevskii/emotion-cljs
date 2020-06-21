(ns helix-emotion.components.button.core
  (:require
   [helix.core :refer [$ defnc]]
   [helix-emotion.components.button.styled :as styled]))

(defnc button
  [{:keys [class-name
           color
           children
           on-click]}]
  {:helix/features {:fast-refresh true}}
  ($ styled/button
     {:class-name class-name
      :color      color
      :on-click   on-click}
     children))
