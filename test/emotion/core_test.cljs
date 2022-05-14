(ns emotion.core-test
  (:require
   ["react" :as react]
   [cljs-bean.core :refer [->clj]]
   [cljs.test :refer [deftest is]]
   [emotion.core :refer [defcss defcss-when defstyled let-css
                         defkeyframes defwithc with-component
                         defmedia]]
   [emotion.helpers :as helpers]))

(def test-component
  (fn [props]
    (react/createElement "div" props)))

(def test-component-with-class-prop
  (fn [props]
    (react/createElement "div"
                         #js {:className (:class (->clj props))})))

(def font-face :sans-serif)

(defcss css-nil
  nil)

(defcss css-simple
  {:color     "red"
   :font-size "1rem"})

(defcss css-with-types
  {:color     :red
   :font-size 14 ;; px
   :font-face font-face})

(defcss css-with-important
  {:color :red!important})

(defcss css-with-props-and-conditions
  #_{:clj-kondo/ignore [:unresolved-symbol]}
  [size active?]
  nil
  {:color :red}
  {:font-size size}
  nil
  (when active?
    {:color :green}))

(defcss css-with-hover-focus
  {:color   :red
   :&:hover {:color :green}
   :&:focus {:color :blue}})

(defcss css-nested
  {:color         :red
   "& .name-test" {:color :blue}})

(defcss-when css-when-simple [active?]
  (not active?)
  {:color :yellow})

(defstyled <wrap> :div
  {:color :red})

(defstyled <header> :header
  css-with-hover-focus
  {:font-size   12
   :line-height 1.6}
  nil
  css-when-simple)

(defstyled <labeling> :div
  {:color :red
   :label :with-label})

(defstyled <with-forward>
  [:div
   {:should-forward-prop (fn [] false)}]
  {})

(defstyled <with-className-prop>
  [test-component
   {:class-name-prop :className}]
  {})

(defstyled <with-class-prop>
  [test-component-with-class-prop
   {:class-name-prop :class}]
  {})

(defstyled <without-camel-casing-props>
  [test-component
   {:camel-casing-props? false}]
  {})

(defcss css-with-let []
  (let-css [size 12]
    {:font-size size}))

(defkeyframes animation
  {"0%, 100%" {:font-size 12}
   "40%"      {:font-size 20}})

(defwithc <h1> <header> :h1)
(defwithc <h2> <header> <wrap>)

(deftest css
  (is (= (helpers/render-styles css-nil)
         "")
      "Render css with nil")

  (is (= (helpers/render-styles css-simple)
         ".css-1yzs1up{color:red;font-size:1rem;}")
      "Render simple css")

  (is (= (helpers/render-styles css-with-types)
         ".css-82yfue{color:red;font-size:14px;font-face:sans-serif;}")
      "Render simple css with keywords, numbers and variables")

  (is (= (helpers/render-styles css-with-important)
         ".css-4weyyp{color:red!important;}")
      "Render simple css with !important")

  (is (= (helpers/render-styles css-with-props-and-conditions)
         ".css-w17a2{color:red;}")
      "Render css with props and conditions. Option #1")

  (is (= (helpers/render-styles (css-with-props-and-conditions {:size 12}))
         ".css-v9w44u{color:red;font-size:12px;}")
      "Render css with props and conditions. Option #2")

  (is (= (helpers/render-styles (css-with-props-and-conditions {:size 12 :active? true}))
         ".css-3ctjxa{color:green;font-size:12px;}")
      "Render css with props and conditions. Option #3")

  (is (= (helpers/render-styles css-with-hover-focus)
         ".css-1m6j18p{color:red;}.css-1m6j18p:hover{color:green;}.css-1m6j18p:focus{color:blue;}")
      "Render css with focus and hover styles")

  (is (= (helpers/render-styles css-nested)
         ".css-pduwh3{color:red;}.css-pduwh3 .name-test{color:blue;}")
      "Render nested css classes in defcss"))

(deftest css-when
  (is (= (helpers/render-styles css-when-simple)
         ".css-kursji{color:yellow;}")
      "Render defcss-when when prop is false")

  (is (= (helpers/render-styles (css-when-simple {:active? true}))
         "")
      "Render defcss-when when prop is true"))

(deftest styled
  (is (= (helpers/render-component <wrap> {})
         {:html "<div class=\"css-cayl1k\"></div>"
          :ids  ["cayl1k"]
          :css  ".css-cayl1k{color:red;}"})
      "Render simple styled component")

  (is (= (helpers/render-component <header> {:active? true})
         {:html "<header class=\"css-gejcei\"></header>"
          :ids  ["gejcei"]
          :css  ".css-gejcei{color:red;font-size:12px;line-height:1.6;}.css-gejcei:hover{color:green;}.css-gejcei:focus{color:blue;}"})
      "Render active styled header component with styles and css.")

  (is (= (helpers/render-component <header> {:active? false})
         {:html "<header class=\"css-prwpvm\"></header>"
          :ids  ["prwpvm"]
          :css  ".css-prwpvm{color:red;font-size:12px;line-height:1.6;color:yellow;}.css-prwpvm:hover{color:green;}.css-prwpvm:focus{color:blue;}"})
      "Render active styled header component with styles and css.")

  (is (= (helpers/render-component <wrap> {:class-name "test-class"})
         {:html "<div class=\"test-class css-cayl1k\"></div>"
          :ids  ["cayl1k"]
          :css  ".css-cayl1k{color:red;}"})
      "Render styled component with passing class using `class-name` prop")

  (is (= (helpers/render-component <wrap> {:className "test-class"})
         {:html "<div class=\"test-class css-cayl1k\"></div>"
          :ids  ["cayl1k"]
          :css  ".css-cayl1k{color:red;}"})
      "Render styled component with passing class using `className` prop")

  (is (= (helpers/render-component <wrap> {:class "test-class"})
         {:html "<div class=\"test-class css-cayl1k\"></div>"
          :ids  ["cayl1k"]
          :css  ".css-cayl1k{color:red;}"})
      "Render styled component with passing class using `class` prop")

  (is (= (helpers/render-component <wrap> {:css {:display :inline}})
         {:html "<div class=\"css-tha20r\"></div>"
          :ids  ["tha20r"]
          :css  ".css-tha20r{color:red;display:inline;}"})
      "Render styled component with passing styles")

  (is (= (helpers/render-component <labeling> {})
         {:html "<div class=\"css-sa4hmd-with-label\"></div>"
          :ids  ["sa4hmd-with-label"]
          :css  ".css-sa4hmd-with-label{color:red;}"})
      "Render styled component with passing class labeling")

  (is (= (helpers/render-component
          <with-forward>
          {:size    12
           :active? true})
         {:html "<div class=\"css-1u8qly9\"></div>"
          :ids  ["1u8qly9"]
          :css  ""})
      "Render styled component with custom forward prop function")

  (is (= (helpers/render-component
          <with-className-prop>
          {})
         {:html "<div class=\"css-1u8qly9\"></div>"
          :ids  ["1u8qly9"]
          :css  ""})
      "Render styled component with className prop")

  (is (= (helpers/render-component
          <with-class-prop>
          {})
         {:html "<div class=\"css-1u8qly9\"></div>"
          :ids  ["1u8qly9"]
          :css  ""})
      "Render styled component with class prop")

  (is (= (helpers/render-component
          <without-camel-casing-props>
          {:autoFocus true
           :className "test"})
         {:html "<div autofocus=\"\" class=\"test css-1u8qly9\"></div>"
          :ids  ["1u8qly9"]
          :css  ""})
      "Render styled component without camel casing props"))

(deftest letcss
  (is (= (helpers/render-styles css-with-let)
         ".css-10sbmph{font-size:12px;}")
      "Render css with using css-let"))

(deftest keyframes
  (is (= (helpers/render-styles animation)
         "@-webkit-keyframes animation-20nvzy{0%,100%{font-size:12px;}40%{font-size:20px;}}@keyframes animation-20nvzy{0%,100%{font-size:12px;}40%{font-size:20px;}}")
      "Render css with using css-let"))

(deftest defwithc-test
  (is (= (helpers/render-component <h1> {})
         {:html "<h1 class=\"css-prwpvm\"></h1>"
          :ids  ["prwpvm"]
          :css  ".css-prwpvm{color:red;font-size:12px;line-height:1.6;color:yellow;}.css-prwpvm:hover{color:green;}.css-prwpvm:focus{color:blue;}"})
      "Render styled component with tag changing"))

(deftest defwithc-with-styled-component-and-custom-class
  (is (= (helpers/render-component <h2> {:class-name "test-class"})
         {:html "<div class=\"test-class css-prwpvm css-cayl1k\"></div>"
          :ids  ["prwpvm" "cayl1k"]
          :css  ".css-prwpvm{color:red;font-size:12px;line-height:1.6;color:yellow;}.css-prwpvm:hover{color:green;}.css-prwpvm:focus{color:blue;}.css-cayl1k{color:red;}"})
      "Render styled component with component changing and adding custom class name"))

(deftest with-component-test
  (is (= (helpers/render-component (with-component <header> :h1) {})
         {:html "<h1 class=\"css-prwpvm\"></h1>"
          :ids  ["prwpvm"]
          :css  ".css-prwpvm{color:red;font-size:12px;line-height:1.6;color:yellow;}.css-prwpvm:hover{color:green;}.css-prwpvm:focus{color:blue;}"})
      "Render styled component with dynamic changing component using with-component"))

(deftest with-component-test-with-styled-component
  (is (= (helpers/render-component (with-component <header> <wrap>) {})
         {:html "<div class=\"css-prwpvm css-cayl1k\"></div>"
          :ids  ["prwpvm" "cayl1k"]
          :css  ".css-prwpvm{color:red;font-size:12px;line-height:1.6;color:yellow;}.css-prwpvm:hover{color:green;}.css-prwpvm:focus{color:blue;}.css-cayl1k{color:red;}"})
      "Render styled component with dynamic changing component to styled component using with-component"))

(defmedia media-css
  ["@media(min-width: 420px)"
   "@media(min-width: 920px)"]
  {:color [:red :green]}
  [{:background  :black
    :font-weight :bold}
   {:background      :red
    :text-decoration :underline}]
  {:font-size [14 16]
   :display   :block})

(deftest media
  (is (= (helpers/render-styles media-css)
         ".css-1u1ywa3{display:block;}@media(min-width: 420px){.css-1u1ywa3{color:red;background:black;font-weight:bold;font-size:14px;}}@media(min-width: 920px){.css-1u1ywa3{color:green;background:red;-webkit-text-decoration:underline;text-decoration:underline;font-size:16px;}}")
      "Render media queries css with using defmedia"))

(defmedia media-with-nil-css
  [nil
   "@media(min-width: 420px)"
   "@media(min-width: 920px)"]
  {:color [:blue :red :green]}
  [{:background :yellow}
   {:background  :black
    :font-weight :bold}
   {:background      :red
    :text-decoration :underline}]
  {:font-size [10 14 16]
   :display   :block})

(deftest media-with-nil
  (is (= (helpers/render-styles media-with-nil-css)
         ".css-1x2qh3p{color:blue;background:yellow;font-size:10px;display:block;}@media(min-width: 420px){.css-1x2qh3p{color:red;background:black;font-weight:bold;font-size:14px;}}@media(min-width: 920px){.css-1x2qh3p{color:green;background:red;-webkit-text-decoration:underline;text-decoration:underline;font-size:16px;}}")
      "Render media queries css with using defmedia and with nil as a first breakpoint"))

;; Global
