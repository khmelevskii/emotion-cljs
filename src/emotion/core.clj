(ns emotion.core
  (:require
   [emotion.util :as util]))

(defn- convert-name
  [component-name]
  (if (keyword? component-name)
    (name component-name)
    component-name))

(defmacro defcss
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
  [sym props condition & css]
  (let [props-sym (gensym "props")]
    `(defn ~sym [~props-sym]
       (let [{:keys ~props} (cljs-bean.core/->clj ~props-sym)]
         (when ~condition
           (-> (mapv #(if (map? %) (util/map->camel-map %) %)
                     (vector ~@css))
               emotion.core/create-css))))))

(defmacro let-css
  [params & css]
  `(let ~params (util/map->camel-map ~@css)))

(defmacro defstyled
  [sym component & styles]
  (let [[component
         options] (if (sequential? component) component [component])
        component (emotion.core/convert-name component)
        options   (util/map->camel-map options)
        styles    (mapv #(if (map? %)
                           (util/map->camel-map %)
                           %)
                        styles)]
    `(def ~sym
       (emotion.core/create-styled ~component ~options ~styles))))

(defmacro defkeyframes
  [sym props]
  (let [props (util/map->camel-map props)]
    `(def ~sym
       (emotion.core/keyframes (cljs-bean.core/->js ~props)))))

(defmacro defwithc
  [sym styled-component component]
  (let [component (emotion.core/convert-name component)]
    `(def ~sym
       (.withComponent ~styled-component ~component))))
