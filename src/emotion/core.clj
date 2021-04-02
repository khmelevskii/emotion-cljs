(ns emotion.core
  (:require
   [emotion.util :as util]))

(defmacro defcss
  "Create Emotion css."
  ([sym css]
   (let [css (util/map->camel-map css)]
     `(def ~sym
        (emotion.core/create-css ~css))))
  ([sym props & css]
   (let [props-sym (gensym "props")]
     `(defn ~sym [~props-sym]
        (let [{:keys ~props} (cljs-bean.core/->clj ~props-sym )]
          (-> (conj {} ~@css)
              util/map->camel-map
              emotion.core/create-css))))))

(defmacro defcss-when
  "Create Emotion css based on component properties."
  [sym props condition & css]
  (let [props-sym (gensym "props")]
    `(defn ~sym [~props-sym]
       (let [{:keys ~props} (cljs-bean.core/->clj ~props-sym)]
         (when ~condition
           (-> (mapv #(if (map? %) (util/map->camel-map %) %)
                     (vector ~@css))
               emotion.core/create-css))))))

(defmacro let-css
  "Helper for define variables inside `defcss`."
  [params & css]
  `(let ~params (util/map->camel-map ~@css)))

(defmacro defstyled
  "Create styled component."
  [sym component & styles]
  (let [[component
         options]    (if (sequential? component) component [component])
        component    (util/convert-component-name component)
        display-name (str sym)
        options      (util/map->camel-map options)
        styles       (mapv #(if (map? %)
                              (util/map->camel-map %)
                              %)
                           styles)]
    `(def ~sym
       (emotion.core/create-styled ~display-name
                                   ~component
                                   ~options
                                   ~styles))))

(defmacro defkeyframes
  "Create css keyframes."
  [sym props]
  (let [props (util/map->camel-map props)]
    `(def ~sym
       (emotion.core/keyframes (cljs-bean.core/->js ~props)))))

(defn- property->media
  [breakpoints initial-css prop value]
  (if (sequential? value)
    (reduce-kv
     (fn [acc index v]
       (let [mq (get breakpoints index)]
         (if (nil? mq)
           (assoc acc prop v)
           (assoc-in acc [mq prop] v))))
     initial-css
     value)
    (assoc initial-css prop value)))

(defn- properties->media
  [breakpoints initial-css properties]
  (reduce-kv
   (fn [acc index v]
     (let [mq (get breakpoints index)]
       (if (nil? mq)
         (into acc v)
         (update acc mq merge v))))
   initial-css
   properties))

(defmacro defmedia
  "Create media queries css."
  [sym breakpoints & props]
  (let [css (->>
             props
             (reduce
              #(if (sequential? %2)
                 (properties->media breakpoints %1 %2)
                 (reduce-kv (partial property->media breakpoints) %1 %2))
              {})
             util/map->camel-map)]
    `(def ~sym
       (emotion.core/create-css ~css))))

(defmacro defwithc
  "Defined new changed component/tag in styled component with
  help of `withComponent`."
  [sym styled-component new-component]
  (let [display-name (str sym)]
    `(def ~sym
       (emotion.core/with-component
         ~styled-component
         ~new-component
         {:display-name ~display-name}))))
