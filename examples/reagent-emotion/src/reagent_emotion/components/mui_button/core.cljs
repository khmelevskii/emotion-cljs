(ns reagent-emotion.components.mui-button.core
  (:require
   [reagent.core :as r]
   [emotion.core :refer [defstyled]]
   ["@material-ui/core/Button" :default muiButton]))

(defstyled MuiButton
  [muiButton {:class-name-prop :className
              :wrap            r/adapt-react-class}]
  {:border-radius "100px!important"})
