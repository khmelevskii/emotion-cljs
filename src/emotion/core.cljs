(ns emotion.core
  (:require-macros [emotion.core])
  (:require
   [cljs-bean.core :refer [->js]]
   ["react" :as react]
   ["@emotion/core" :as emotion]
   ["@emotion/styled" :as styled]
   ["@emotion/is-prop-valid" :as is-prop-valid]
   [emotion.props :as p]
   [emotion.util :as util]))

(def ^:private create-element react/createElement)
(def ^:private styled-component (.-default styled))
(def ^:private prop-valid? (.-default is-prop-valid))

(def ^:private emotion-class-prop "className")
(def ^:private class-prop "class-name")

(defn- html-tag?
  "Simple check that component which we need to styled is a simple html tag."
  [component]
  (= (type component) js/String))

(defn- emotion?
  "Check that component is emotion component."
  [component]
  (some? (.-__emotion_base component)))

(defn- styled-display-name
  "Build react displayName for styled components."
  [name]
  (str "Styled(" name ")"))

(defn- object->camel-props
  "Convert keys of js object to camel case react props."
  [props]
  (.reduce (.keys js/Object props)
           (fn [acc prop-name]
             (let [new-prop-name (if (.includes p/camel-props prop-name)
                                   (util/string->camel-case prop-name)
                                   prop-name)]
               (when (prop-valid? new-prop-name)
                 (let [prop-value (aget props prop-name)
                       prop-value (if (and (= new-prop-name emotion-class-prop)
                                           (some? (aget acc emotion-class-prop)))
                                    (str (aget acc emotion-class-prop) " " prop-value)
                                    prop-value)]
                   (aset acc new-prop-name prop-value))))
             acc) #js{}))

(defn- convert-class-name
  "Get component properties with className which converted to
  `class-name` kebab-case style."
  [props]
  (.reduce
   (.keys js/Object props)
   (fn [acc prop-name]
     (let [new-prop-name (if (= prop-name emotion-class-prop)
                           class-prop
                           prop-name)]
       (aset acc new-prop-name (aget props prop-name)))
     acc) #js {}))

(defn- create-styled [display-name component options styles]
  "Create styled component."
  (let [wrapper-component
        (cond
          (emotion? component)  component
          (html-tag? component) #(create-element component
                                                 (object->camel-props %))
          :else                 #(create-element component
                                                 (convert-class-name %)))]
    (aset wrapper-component "displayName"
          (styled-display-name display-name))
    ((styled-component wrapper-component (->js options))
     (fn [props]
       (.concat (->js styles) (.-css props))))))

(defn create-css
  "Create Emotion css."
  [styles]
  (emotion/css (->js styles)))

(def keyframes emotion/keyframes)

(defn Global [props]
  "Add global css."
  (.render emotion/Global
           #js {:styles (.-children props)}))
(set! (.-displayName Global) "GlobalStyled")
