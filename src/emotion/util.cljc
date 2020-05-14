(ns emotion.util
  (:require
   [clojure.string :as string]))

(defn- kwd->camel-case
  "Returns camel case version of the keyword, e.g. :font-size
  becomes \"fontSize\"."
  [value]
  (if (keyword? value)
    (let [[first-word & words] (string/split (name value) #"-")]
      (-> (map string/capitalize words)
          (conj first-word)
          string/join))
    value))

(defn map->camel-map
  "Convert keys of map to camel case version."
  [props]
  (reduce-kv
   #(assoc %1 (kwd->camel-case %2)
           (if (map? %3)
             (map->camel-map %3)
             %3))
   {}
   props))
