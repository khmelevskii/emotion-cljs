(ns emotion.fixtures.performance
  (:require
   [emotion.core :refer [defstyled defcss-when defcss defkeyframes css]]))

(def color-white "#FFFFFF")
(def color-grayscale-700 "#4B565C")
(def color-grayscale-300 "#96A0A6")
(def color-destruction-300 "#F99B9A")
(def color-destruction-200 "#F99B9A")

(defcss text-340 []
  {:font-weight 400
   :font-size   14
   :line-height "20px"})

(defcss text-240 []
  {:font-weight 400
   :font-size 12
   :line-height "18px"})

(defcss-when --on-dark-loader-solid-primary [view]
  (= view "on-dark-solid-primary")
  {:color color-white})

(defcss-when --on-dark-loader-solid-dark [view]
  (= view "on-dark-solid-dark")
  {:color color-grayscale-700})

(defcss-when --on-dark-loader-solid-destruction [view]
  (= view "on-dark-solid-destruction")
  {:color color-destruction-200})

(defcss-when --on-dark-loader-simple-dark [view]
  (= view "on-dark-simple-dark")
  {:color color-grayscale-300})

(defcss-when --on-light-loader-solid-dark [view]
  (= view "on-light-solid-dark")
  {:color color-white})

(defcss-when --on-light-loader-solid-destruction [view]
  (= view "on-light-solid-destruction")
  {:color color-white})

(defcss-when --on-light-loader-outline-dark [view]
  (= view "on-light-outline-dark")
  {:color color-grayscale-300})

(defcss-when --on-light-loader-simple-dark [view]
  (= view "on-light-simple-dark")
  {:color color-grayscale-300})

(defcss-when --on-light-loader-simple-destruction [view]
  (= view "on-light-simple-destruction")
  {:color color-destruction-300})

(defcss-when --size-500 [size children]
  (= size 500)
  text-340
  {:border-radius 6}
  (if (nil? children)
    (css
     {:padding 12

      "& > .__button-icon > svg" {:margin 0}})
    (css {:padding "10px 16px"})))

(defcss-when --size-400 [size children]
  (= size 400)
  text-340
  {:border-radius 6}
  (if (nil? children)
    (css
     {:padding 8

      "& > .__button-icon > svg" {:margin 0}})
    (css {:padding "6px 12px"})))

(defcss-when --size-300 [size children]
  (= size 300)
  text-240
  {:border-radius 4}
  (if (nil? children)
    (css
     {:padding 5

      "& > .__button-icon > svg" {:margin 0}})
    (css {:padding "3px 8px"})))

(defkeyframes loader
  {"0%, 100%" {:box-shadow "0 12px 0 -5px"}
   "40%"      {:box-shadow "0 12px 0 0"}})

(defstyled Loader :div
  {:position :absolute
   :top      "50%"
   :left     "50%"

   "&, &:before, &:after"
   {:border-radius       "50%"
    :width               6
    :height              6
    :animation-fill-mode :both
    :animation           (str loader " 1.4s infinite ease-in-out")}

   :&
   {:text-indent     "-9999em"
    :transform       "translate(-50%, -50%)"
    :margin-top      -12
    :animation-delay "-0.12s"}

   "&:before, &:after"
   {:content  "\"\""
    :position :absolute
    :top      0}

   :&:before
   {:left            (* (+ 6 6) -1)
    :animation-delay "-0.32s"}

   :&:after
   {:left (+ 6 6)}}
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
  --on-light-loader-simple-destruction)
