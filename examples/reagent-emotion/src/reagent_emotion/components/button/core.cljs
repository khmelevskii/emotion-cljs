(ns reagent-emotion.components.button.core
  (:require
   [reagent-emotion.components.button.styled :as styled]))

(defn Button
  [{:keys [class-name
           color
           on-click]}
           children]
  [styled/Button
   {:class-name class-name
    :color      color
    :on-click   on-click}
   children])
