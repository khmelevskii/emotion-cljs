(ns helix-emotion.components.mui-button.core
  (:require
   [emotion.core :refer [defstyled]]
   ["@mui/material/Button" :default muiButton]))

(defstyled mui-button
  [muiButton {:class-name-prop :className}]
  {:border-radius "100px!important"})
