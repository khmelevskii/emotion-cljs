(ns emotion.performance-test
  (:require
   ["react" :as react]
   ["react-dom/server" :as server]
   ["@emotion/react" :as emotion-react]
   ["@emotion/cache" :default create-cache]
   [emotion.fixtures.performance-native :refer [LoaderNative]]
   [emotion.fixtures.performance :refer [Loader]]))

(defn measure-native-renders []
  (let [cache' (create-cache #js {:key "css"})]
    (js/performance.mark "measure-render-start")
    (dotimes [_ 100000]
      (.renderToString
       server
       (react/createElement
        (.-CacheProvider emotion-react)
        #js {:value    cache'
             :children (react/createElement
                        LoaderNative
                        #js {:className "test-class"
                             :view      "on-dark-solid-primary"
                             :size      500})})))
    (js/performance.mark "measure-render-end")
    (.-duration
     (js/performance.measure "measure-render"
                             "measure-render-start"
                             "measure-render-end"))))

(defn measure-renders []
  (let [cache' (create-cache #js {:key "css"})]
    (js/performance.mark "measure-render-start")
    (dotimes [_ 100000]
      (.renderToString
       server
       (react/createElement
        (.-CacheProvider emotion-react)
        #js {:value    cache'
             :children (react/createElement
                        Loader
                        #js {:className "test-class"
                             :view      "on-dark-solid-primary"
                             :size      500})})))
    (js/performance.mark "measure-render-end")
    (.-duration
     (js/performance.measure "measure-render"
                             "measure-render-start"
                             "measure-render-end"))))

(do
  (println "Native:" (measure-native-renders))
  (println "EmotionCljs:" (measure-renders)))
