(ns emotion.helpers
  (:require
   [cljs-bean.core :refer [->js ->clj]]
   ["@emotion/core" :as core]
   ["@emotion/styled" :as styled]
   ["@emotion/cache" :as cache]
   ["create-emotion-server" :as create-emotion-server]
   ["react" :as react]
   ["react-dom/server" :as server]))

(defn render-styles [styles]
  (let [cache'         (.default cache)
        emotion-server (.default create-emotion-server cache')

        html (.renderToString
              server
              (react/createElement
               (.-CacheProvider core)
               (->js
                {:value    cache'
                 :children (react/createElement
                            (.div (.-default styled) styles))})))]
    (->> html
         (.extractCritical emotion-server)
         .-css)))

(defn render-component [component props]
  (let [cache'         (.default cache)
        emotion-server (.default create-emotion-server cache')

        html (.renderToString
              server
              (react/createElement
               (.-CacheProvider core)
               (->js
                {:value    cache'
                 :children (react/createElement
                            component
                            (->js props))})))]
    (->clj
     (.extractCritical emotion-server html))))
