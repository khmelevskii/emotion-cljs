(ns emotion.helpers
  (:require
   [cljs-bean.core :refer [->js ->clj]]
   ["@emotion/react" :as emotion-react]
   ["@emotion/styled" :as styled]
   ["@emotion/cache" :default create-cache]
   ["@emotion/server/create-instance" :default create-emotion-server]
   ["react" :as react]
   ["react-dom/server" :as server]))

(defn render-styles [styles]
  (let [cache'         (create-cache #js {:key "css"})
        emotion-server (create-emotion-server cache')

        html (.renderToString
              server
              (react/createElement
               (.-CacheProvider emotion-react)
               (->js
                {:value    cache'
                 :children (react/createElement
                            (.div (.-default styled) styles))})))]
    (->> html
         (.extractCritical emotion-server)
         .-css)))

(defn render-component [component props]
  (let [cache'         (create-cache #js {:key "css"})
        emotion-server (create-emotion-server cache')

        html (.renderToString
              server
              (react/createElement
               (.-CacheProvider emotion-react)
               (->js
                {:value    cache'
                 :children (react/createElement
                            component
                            (->js props))})))]
    (->clj
     (.extractCritical emotion-server html))))
