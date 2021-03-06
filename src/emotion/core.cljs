(ns emotion.core
  (:require-macros [emotion.core])
  (:require
   [cljs-bean.core :refer [->js]]
   ["react" :as react]
   ["@emotion/react" :as emotion-react]
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
  ([component fn-convert]
   (create-forwarded-element component fn-convert component))
  ([component fn-convert display-name]
   (let [component-wrapper
         #(create-element
           component
           (.assign js/Object
                    (fn-convert %1)
                    #js {:ref %2}))]
     (aset component-wrapper "displayName" display-name)
     (forward-ref component-wrapper))))

(defn- convert-class-name
  "Convert component properties with `className` or `class` which
  will converted to `class-name` kebab-case style."
  ([props]
   (convert-class-name props default-class-prop))
  ([props class-name-prop]
   (.reduce
    (.keys js/Object props)
    (fn [acc prop-name]
      (if (valid-class-props prop-name)
        (aset acc class-name-prop
              (if-let [current-class-name (aget acc class-name-prop)]
                (str current-class-name " " (aget props prop-name))
                (aget props prop-name)))
        (aset acc prop-name (aget props prop-name)))
      acc) #js {})))

(defn- create-styled
  "Create styled component."
  [display-name component options styles]
  (let [wrap                (get options "wrap")
        camel-casing-props? (get options "camelCasingProps?" true)
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
    (aset wrapper-component "displayName" display-name)
    (let [result ((styled-component wrapper-component (->js options))
                  (fn [props]
                    (.concat (->js styles) (.-css props))))]
      (if wrap
        (wrap result)
        result))))

(defn create-css
  "Create Emotion css."
  [styles]
  (emotion-react/css (->js styles)))

(def keyframes emotion-react/keyframes)

(defn with-component
  "Change component/tag in styled component with help of `withComponent`."
  ([styled-component new-component]
   (with-component styled-component new-component nil))
  ([styled-component new-component {:keys [display-name]}]
   (let [new-component     (util/convert-component-name new-component)
         html-tag?         (html-tag? new-component)
         component-wrapper (create-forwarded-element
                            new-component
                            (if html-tag?
                              object->camel-props
                              convert-class-name)
                            display-name)]
     (when-not html-tag?
       (aset component-wrapper "defaultProps"
             (.-defaultProps new-component))
       (aset component-wrapper "displayName"
             (or display-name (.-displayName new-component))))
     (.withComponent styled-component component-wrapper))))

(defn Global
  "Add global css."
  [props]
  (.render emotion-react/Global
           #js {:styles (.-children props)}))
(set! (.-displayName Global) "GlobalStyled")
