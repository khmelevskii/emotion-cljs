(ns emotion.core
  (:require
   [emotion.util :as util]))

(defn- convert-name
  "Convert keyworded component name to string."
  [component-name]
  (if (keyword? component-name)
    (name component-name)
    component-name))

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
        component    (emotion.core/convert-name component)
        display-name (str component)
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
  "Change component/tag in styled component with help of `withComponent`."
  [sym styled-component component]
  (let [component           (emotion.core/convert-name component)
        convert-class-name? (gensym "convert-class-name?")
        component-wrapper   (gensym "component-wrapper")]
    `(def ~sym
       (let [~convert-class-name? (not (html-tag? ~component))
             ~component-wrapper   (if ~convert-class-name?
                                    #(create-element ~component
                                                     (convert-class-name %))
                                    ~component)]
         (when ~convert-class-name?
           (aset ~component-wrapper "defaultProps"
                 (.-defaultProps ~component))
           (aset ~component-wrapper "displayName"
                 (styled-display-name (.-displayName ~component))))
         (.withComponent ~styled-component ~component-wrapper)))))
