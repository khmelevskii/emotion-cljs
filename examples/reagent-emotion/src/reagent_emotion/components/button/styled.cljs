(ns reagent-emotion.components.button.styled
  (:require
   [reagent.core :as r]
   [emotion.core :refer [defcss-when defstyled]]))

(defcss-when --color-primary [color]
  (= color "primary")
  {:color            "#fff"
   :background-color "#006cda"

   :&:hover {:background-color "#3690df"}})

(defcss-when --color-secondary [color]
  (= color "secondary")
  {:color            "#fff"
   :background-color "#068d63"

   :&:hover {:background-color "#25c186"}})

(defstyled Button
  [:button
   {:wrap r/adapt-react-class}]
  {:font-family   "Arial"
   :font-size     18
   :border        :none
   :padding       "8px 16px"
   :border-radius 4
   :cursor        :pointer}
  --color-primary
  --color-secondary)
