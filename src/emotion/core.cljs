(ns emotion.core
  (:require-macros [emotion.core])
  (:require
   [goog.object]
   [cljs-bean.core :refer [->js]]
   ["@emotion/core" :as emotion]
   ["@emotion/styled" :as styled]
   [emotion.util]))

(defn- create-styled [component options styles]
  ((.default styled component (->js options))
   (fn [props]
     (.concat (->js styles) (.-css props)))))

(defn create-css
  [styles]
  (emotion/css (->js styles)))

(def keyframes emotion/keyframes)

(defn Global [props]
  (.render emotion/Global
           #js {:styles (goog.object/get props "children")}))
(set! (.-displayName Global) "GlobalStyled")
