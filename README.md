<p align="center" style="color: #343a40">
  <img src="https://cdn.rawgit.com/tkh44/emotion/master/emotion.png" alt="emotion" height="150" width="150">
  <h1 align="center">emotion-cljs</h1>
</p>
<p align="center" style="font-size: 1.2rem;">
  ClojureScript interface for <a href="https://github.com/emotion-js/emotion" target="_blank">Emotion</a>
  <br>
  (The Next Generation of CSS-in-JS)
</p>

[![Clojars Project](https://img.shields.io/clojars/v/emotion-cljs.svg)](https://clojars.org/emotion-cljs)
![](https://github.com/khmelevskii/emotion-cljs/workflows/GitHub%20Actions/badge.svg)

emotion-cljs is tiny wrapper for Emotion - performant and flexible CSS-in-JS javascript library.
emotion-cljs is set of macro for useful bindings to Emotion features with minimal overhead.

## Problem
In JavaScript realm there are popular and convenient appraches for working with CSS, it's `CSS modules` and `CSS-in-JS` libs.
CSS modules is allowed to write namespaced CSS and forgot about slelectors conflicts. CSS-in-JS libs very popular in `React` ecosystem,
which helps to resolve problem with namespacing of css and also helps to incapsulate logic of applying css rules based on
props of component. It helps to remove this logic from render and have much cleaner render.
Emotion is great CSS-IN-JS solution in JavaScript. This library is provide simple interface to work with her with minimal overhead.

В javascript мире, для работы с CSS хорошо себя зарекомендовали и достаточно удобны в использовании css-modules и CSS-in-JS
библиотеки. css модули позволяют писать css классы каждый в своем неймспейсе и не заботится о проблемы пересечения имен css классов.
css-in-js библиотеки, применимые для React приложений, кроме решения проблемы, которую решают css-modules, также позволяет инкапуслировать логику
генерации css на основе свойств реакт-компонентов, что в свою очередь помогает сделать рендер чише и избавится от логики, которая описывает стили.
Emotion - одно из лучших css-in-js решений в javascript мире, а эта библиотека, предоставляет простой интерфейс работы с ней в clojurescript.

## Documentation

### `defstyled`
`defstyled` macro expands into `@emotion/styled` and uses for creating React components that have styles attached to them.

https://emotion.sh/docs/styled

Here is example of usage:
```clojure
(defstyled Button :button
  {:display :flex
   :color :red})
```

Which will compile to JS emotion code:
```javascript
const Button = styled.button`
  display: flex;
  color: red;
`
```

You are able to mix css with conditions in styled components to simplify and incapsulate logic of css rules.
Please check `defcss` and `defcss-when`.

Also you can use keywords anywhere where it's possible or strings in any places:
```clojure
(defstyled Button :button
  {"display" "flex"
   :font-size 14 ;; 14px
   :text-align :center!important
   :line-height "2rem"})
```

And a few examples of using nested css selectors:
```clojure
(defstyled Button :button
  {:color :red
  
   :&:hover {:color :blue}
   "&:before, &:after" {:color :green}
   "& > span" {:color :brown}
   :&+& {:color :orange}})
```


#### Labels
Emotion adds a css property called `label`, the value of it will appended to the end of the class name. It can be more readable than a hash.

https://emotion.sh/docs/labels
```clojure
(defstyled Label :div
  {:color :red
   :label :with-label})
;; will generate class, like `css-moghuq-with-label` 
```


#### Customizing prop forwarding
By default, Emotion passes all props to custom component and only props that are valid of html attributes for tags.
You can customize this behavior by passing `:should-forward-prop` option.

https://emotion.sh/docs/styled#customizing-prop-forwarding
```clojure
(defstyled button
  [:div
   {:should-forward-prop (fn [] false)}]
  {})
```


### `defcss`
It's way to union css rules to logic parts, based on component props if it's needed, and combine this parts inside `defstyled`. 
Basic usage:
```clojure
(defcss primary
  {:color :blue})
```

And a few examples with component props:
```clojure
(defcss secondary
  [size active?]
  {:color :red}
  {:font-size size}
  (when active?
    {:color :green}))
```


### `defcss-when`
It's just shortcut for `defcss` with `when` inside body.
```clojure
(defcss-when disabled [disabled?]
  disabled?
  {:color :gray})
```
this example if equivalent of
```clojure
(defcss disabled [disabled?]
  (when disabled
    {:color :gray}))
```


### `let-css`
This macro is useful in cases when you need to define local variables inside `defcss` or `defstyled`.
```clojure
(defcss checkbox []
  (let-css [size 12]
    {:font-size size
     :border-radius (/ size 4)}))
```


### `defkeyframes`
Macro for define css keyframes.

https://emotion.sh/docs/keyframes
```clojure
(defkeyframes animation
  {"0%, 100%" {:font-size 12}
   "40%"      {:font-size 20}})
```


### `defmedia`
TODO


### `defwithc`
Change the rendered tag. Sometimes you want to create some styles with one component but then use
those styles again with another component. `defwithc` method can be used in this case.

https://emotion.sh/docs/styled#change-the-rendered-tag-using-withcomponent
```clojure
(defstyled Header :h1
  {:font-size 14})

(defwithc Subheader Header :h2)
```


### `Global`
Sometimes you might want to insert global css like resets or font faces. ou can use the `Global` macro to do this.

https://emotion.sh/docs/globals

Example with helix:
```clojure
($ Global reset-styles global-styles)
```

### Passing className
You are able to pass className prop to styled component using the following props:
`:class-name`, `:class` or `:className`. All of them will convert to `className`
when will pass to React component.

### Complex real world example
```clojure
(defkeyframes loader
  {"0%, 100%" {:box-shadow "0 12px 0 -5px"}
   "40%"      {:box-shadow "0 12px 0 0"}})

(defcss-when --primary [view pure?]
  (and (not pure?)
       (= type :primary))
  {:background-color cl/primary-500
   :color            cl/white})

(defcss-when --secondary [view pure?]
  (and (not pure?)
       (= type :secondary))
  {:background-color cl/secondary-500
   :color            cl/black})
   
(defcss-when --loading [loading?]
  loading?
  {:position :relative
   :animation-fill-mode :both
   :animation           (str loader " 1.4s infinite ease-in-out")}})
   
(defstyled <Button> :button
  t/text-500 ;; global variable from design system
  {:border          :none
   :cursor          :pointer
   :outline         :none
   :text-decoration :none
   :transition      (str "background-color" a/fast-transition ","
                         "color" a/fast-transition ","
                         "box-shadow" a/fast-transition)}
  --primary
  --secondary
  --loading)
```

## Roadmap
* Targeting another emotion component. https://emotion.sh/docs/styled#targeting-another-emotion-component
* Server-side rendering. PR is welcome!
* Theming? PR is welcome!


## License
Copyright © 2020 Yurii Khmelevskyi

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
