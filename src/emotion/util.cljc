(ns emotion.util
  (:require
   [clojure.string :as string]))

(defn string->camel-case
  "Returns camel case version of the string, e.g. \"font-size\"
  becomes \"fontSize\"."
  [value]
  (let [[first-word & words] (string/split value #"-")]
    (if (or (= first-word "data")
            (= first-word "area"))
      value
      (-> (map string/capitalize words)
          (conj first-word)
          string/join))))

(defn kwd->camel-case
  "Returns camel case version of the keyword, e.g. :font-size
  becomes \"fontSize\"."
  [value]
  (if (keyword? value)
    (string->camel-case (name value))
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
