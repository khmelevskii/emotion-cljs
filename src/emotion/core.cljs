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
(def ^:private forward-ref react/forwardRef)
(def ^:private styled-component (.-default styled))
(def ^:private prop-valid? (.-default is-prop-valid))

(def ^:private valid-class-props #{"class-name" "className" "class"})
(def ^:private emotion-class-prop "className")
(def ^:private default-class-prop "class-name")

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
             (let [new-prop-name (cond
                                   ;; class props
                                   (valid-class-props prop-name)
                                   emotion-class-prop
                                   ;; camelCase props
                                   (.includes p/camel-props prop-name)
                                   (util/string->camel-case prop-name)
                                   :else prop-name)]
               (when (prop-valid? new-prop-name)
                 (let [prop-value (aget props prop-name)
                       prop-value (if (and (= new-prop-name emotion-class-prop)
                                           (some? (aget acc emotion-class-prop)))
                                    (str (aget acc emotion-class-prop) " " prop-value)
                                    prop-value)]
                   (aset acc new-prop-name prop-value))))
             acc) #js{}))

(defn- create-forwarded-element
  "Create React component wrapped with React.forwardRef"
  [component fn-convert]
  (forward-ref
   #(create-element
     component
     (.assign js/Object
              (fn-convert %1)
              #js {:ref %2}))))

(defn- convert-class-name
  "Convert component properties with `className` or `class` which
  will converted to `class-name` kebab-case style."
  ([props]
   (convert-class-name props default-class-prop))
  ([props class-name-prop]
   (.reduce
    (.keys js/Object props)
    (fn [acc prop-name]
      (let [new-prop-name (if (valid-class-props prop-name)
                            class-name-prop
                            prop-name)]
        (aset acc new-prop-name (aget props prop-name)))
      acc) #js {})))

(defn- create-styled [display-name component options styles]
  "Create styled component."
  (let [camel-casing-props? (get options "camelCasingProps?" true)
        class-name-prop     (name (get options "classNameProp"
                                       default-class-prop))
        wrapper-component
        (cond
          (not camel-casing-props?) component
          (emotion? component)      component
          (html-tag? component)     (create-forwarded-element
                                     component
                                     object->camel-props)
          (= class-name-prop
             emotion-class-prop)    component
          :else                     (create-forwarded-element
                                     component
                                     #(convert-class-name % class-name-prop)))]
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
