(ns emotion.core
  (:require
   [clojure.string]
   [cljs.tagged-literals]
   [emotion.util :as util]))

(defn ^:private map->js
  [css]
  (if (map? css)
    (util/map->camel-object css)
    css))

(defmacro defcss
  "Create Emotion css."
  [sym props & css]
  (cond
    ;; when no variables and single css object
    (and (empty? props)
         (= (count css) 1)
         (map? (first css)))
    (let [res (map->js (first css))]
      `(def ~sym (emotion.core/create-css ~res)))

    ;; when no params and more then 1 css
    (and (empty? props)
         (> (count css) 1))
    (let [res (cljs.tagged-literals/read-js
               (mapv map->js css))]
      `(def ~sym
         (emotion.core/create-css ~res)))
    ;; when params and one or more css
    :else
    (let [props-sym (gensym "props")
          res       (cljs.tagged-literals/read-js
                     (mapv map->js css))]
      `(defn ~sym [~props-sym]
         (let [{:keys ~props} (cljs-bean.core/->clj ~props-sym)]
           (emotion.core/create-css ~res))))))

(defmacro defcss-when
  "Create Emotion css based on component properties."
  [sym props condition & css]
  (let [props-sym (gensym "props")
        res       (cljs.tagged-literals/read-js
                   (mapv map->js css))]
    `(defn ~sym [~props-sym]
       (let [{:keys ~props} (cljs-bean.core/->clj ~props-sym)]
         (when ~condition
           (emotion.core/create-css ~res))))))

(defmacro css
  "Covert cljs map to valid css object."
  [css]
  (let [res (util/map->camel-object css)]
    `~res))

(defmacro defstyled
  "Create styled component."
  [sym component & styles]
  (let [[component
         options]    (if (sequential? component) component [component])
        component    (util/convert-component-name component)
        display-name (str sym)
        label        (str
                      (clojure.string/replace (str *ns*) #"\." "-")
                      "-"
                      (clojure.string/replace display-name #"[<>]" ""))
        options      (util/map->camel-object options)
        styles       (cljs.tagged-literals/read-js
                      (mapv #(if (map? %)
                               (util/map->camel-object %)
                               %)
                            styles))]
    `(def ~sym
       (emotion.core/create-styled ~display-name
                                   ~component
                                   ~options
                                   ~styles
                                   ~label))))

(defmacro defkeyframes
  "Create css keyframes."
  [sym props]
  (let [props (util/map->camel-object props)]
    `(def ~sym
       (emotion.core/keyframes ~props))))

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
             util/map->camel-object)]
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
