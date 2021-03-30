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
