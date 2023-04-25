(ns emotion.fixtures.performance-native
  (:require
   ["@emotion/react" :as emotion-react]
   ["@emotion/styled" :default styled]))

(def color-white "#FFFFFF")
(def color-grayscale-700 "#4B565C")
(def color-grayscale-300 "#96A0A6")
(def color-destruction-300 "#F99B9A")
(def color-destruction-200 "#F99B9A")

(def text-340
  (emotion-react/css
   #js {:fontWeight 400
        :fontSize   14
        :lineHeight "20px"}))

(def text-240
  (emotion-react/css
   #js {:fontWeight 400
        :fontSize 12
        :lineHeight "18px"}))

(defn --on-dark-loader-solid-primary [props]
  (when (= (.-view props) "on-dark-solid-primary")
    (emotion-react/css
     #js {:color color-white})))

(defn --on-dark-loader-solid-dark [props]
  (when (= (.-view props) "on-dark-solid-dark")
    (emotion-react/css
     #js {:color color-grayscale-700})))

(defn --on-dark-loader-solid-destruction [props]
  (when (= (.-view props) "on-dark-solid-destruction")
    (emotion-react/css
     #js {:color color-destruction-200})))

(defn --on-dark-loader-simple-dark [props]
  (when (= (.-view props) "on-dark-simple-dark")
    (emotion-react/css
     #js {:color color-grayscale-300})))

(defn --on-light-loader-solid-dark [props]
  (when (= (.-view props) "on-light-solid-dark")
    (emotion-react/css
     #js {:color color-white})))

(defn --on-light-loader-solid-destruction [props]
  (when (= (.-view props) "on-light-solid-destruction")
    (emotion-react/css
     #js {:color color-white})))

(defn --on-light-loader-outline-dark [props]
  (when (= (.-view props) "on-light-outline-dark")
    (emotion-react/css
     #js {:color color-grayscale-300})))

(defn --on-light-loader-simple-dark [props]
  (when (= (.-view props) "on-light-simple-dark")
    (emotion-react/css
     #js {:color color-grayscale-300})))

(defn --on-light-loader-simple-destruction [props]
  (when (= (.-view props) "on-light-simple-destruction")
    (emotion-react/css
     #js {:color color-destruction-300})))

(defn --size-500 [props]
  (when (= (.-size props) 500)
    (emotion-react/css
     (let [result #js {}]
       (js/Object.assign result
                         text-340
                         #js {:borderRadius 6}
                         (if (nil? (.-children props))
                           #js {:padding                   12
                                "& > .__button-icon > svg" #js {:margin 0}}
                           #js {:padding "10px 16px"}))
       result))))

(defn --size-400 [props]
  (when (= (.-size props) 400)
    (emotion-react/css
     (let [result #js {}]
       (js/Object.assign result
                         text-340
                         #js {:borderRadius 6}
                         (if (nil? (.-children props))
                           #js {:padding                   8
                                "& > .__button-icon > svg" #js {:margin 0}}
                           #js {:padding "6px 12px"}))
       result))))

(defn --size-300 [props]
  (when (= (.-size props) 300)
    (emotion-react/css
     (let [result #js {}]
       (js/Object.assign result
                         text-240
                         #js {:borderRadius 4}
                         (if (nil? (.-children props))
                           #js {:padding                   5
                                "& > .__button-icon > svg" #js {:margin 0}}
                           #js {:padding "3px 8px"}))
       result))))

(def loader
  (emotion-react/keyframes
   #js {"0%, 100%" #js {:boxShadow "0 12px 0 -5px"}
        "40%"      #js {:boxShadow "0 12px 0 0"}}))

(def LoaderNative
  ((styled "div" "Loader")
   (fn [props]
     (let [result
           #js [(let [size 6]
                  #js {:position "absolute"
                       :top      "50%"
                       :left     "50%"

                       "&, &:before, &:after"
                       #js {:borderRadius      "50%"
                            :width             size
                            :height            size
                            :animationFillMode "both"
                            :animation         (str loader " 1.4s infinite ease-in-out")}

                       :&
                       #js {:textIndent     "-9999em"
                            :transform      "translate(-50%, -50%)"
                            :marginTop      -12
                            :animationDelay "-0.12s"}

                       "&:before, &:after"
                       #js {:content  "\"\""
                            :position "absolute"
                            :top      0}

                       :&:before
                       #js {:left           (* (+ size 6) -1)
                            :animationDelay "-0.32s"}

                       :&:after
                       #js {:left (+ size 6)}})
                --size-500
                --size-400
                --size-300
                --on-dark-loader-solid-primary
                --on-dark-loader-solid-dark
                --on-dark-loader-solid-destruction
                --on-dark-loader-simple-dark
                --on-light-loader-solid-dark
                --on-light-loader-solid-destruction
                --on-light-loader-outline-dark
                --on-light-loader-simple-dark
                --on-light-loader-simple-destruction]]
       (.concat result (.-css props))))))
