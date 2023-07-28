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
emotion-cljs is set of macros for useful bindings to Emotion features with minimal overhead.

## Problem
In JavaScript realm there are popular and convenient approaches for working with CSS, it's `CSS modules` and `CSS-in-JS` libs.
CSS modules allow to write namespaced CSS and forget about selectors conflicts. CSS-in-JS libs are very popular in `React` ecosystem and
 help to resolve problems with namespacing in css and also incapsulate logic of applying css rules based on
props of component. It helps to remove this logic from render and make it much cleaner.
Emotion is a great CSS-IN-JS solution in JavaScript. emotion-cljs library provides simple interface to work with Emotion with minimal overhead.

## Documentation

### Installation
This library will require `@emotion/react` and `@emotion/styled` from node_modules:
```bash
yarn add @emotion/react @emotion/styled
# or
npm i @emotion/react @emotion/styled
```


### `defstyled`
`defstyled` macro expands into `@emotion/styled` and is used for creating React components that have styles attached to them.

https://emotion.sh/docs/styled

Here is an example of usage:
```clojure
(defstyled Button :button
  {:display :flex
   :color :red})
```

Which will be compiled to JS emotion code:
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
By default, in dev mode, emotion-cljs adds a namespace label for each generated class. If you need, Emotion provides the possibility to add a custom label which will be appended to the CSS class name.
https://emotion.sh/docs/labels
```clojure
(defstyled Label :div
  {:color :red
   :label :with-label})
;; will generate class, like `css-moghuq-with-label` 
```


#### Customizing prop forwarding
By default, Emotion passes all props to a component and only those props that are valid html attributes for tags.
You can customize this behavior by passing `:should-forward-prop` option.

https://emotion.sh/docs/styled#customizing-prop-forwarding
```clojure
(defstyled button
  [:div
   {:should-forward-prop (fn [] false)}]
  {})
```

#### Wrapping styled components
In case when you need to wrap styled component, for example, for using in Reagent, you need to wrap native React component in `reagent.core/adapt-react-class`. You can do it with the help of `:wrap` option.

```clojure
(defstyled button
  [:div
   {:wrap reagent.core/adapt-react-class}]
  {})
```

### `defcss`
It's a way to unite css rules into logic parts, based on component props if it's needed, and combine this parts inside of `defstyled`. 
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
It's just a shortcut for `defcss` with `when` inside body.
```clojure
(defcss-when disabled [disabled?]
  disabled?
  {:color :gray})
```
this example is an equivalent of
```clojure
(defcss disabled [disabled?]
  (when disabled
    {:color :gray}))
```


### `css`
This macro needs to be used when you need to define styles inside some functions, for example, `when`, `case`, etc.
```clojure
(defcss-when checkbox [theme]
  (case theme
    "light" (css {:color :green})
    "dark"  (css {:color :red})))
```


### `defkeyframes`
Macro for defining css keyframes.

https://emotion.sh/docs/keyframes
```clojure
(defkeyframes animation
  {"0%, 100%" {:font-size 12}
   "40%"      {:font-size 20}})
```


### `defmedia`
With the help of this macro it's easy to work with media queries. Interface of `defmedia` was inspired by
facepaint library.

https://github.com/emotion-js/facepaint

```clojure
(defmedia --responsive-header
  ["@media(min-width: 420px)"
   "@media(min-width: 920px)"]
  {:display   :block
   :color     [:green :blue]
   :font-size [16 20]})
```

Output of it will be:

```css
{
    display: block;

    '@media(min-width: 420px)': {
        color: green;
        font-size: 16px;
    }

    '@media(min-width: 920px)': {
        color: green;
        font-size: 20px;
    }
}
```

`defmedia` also supports using a list of css properties for a breakpoint:

```clojure
(defmedia --responsive-header
  ["@media(min-width: 420px)"
   "@media(min-width: 920px)"]
  {:display :block}
  [{:color :green
    :font-size 16}
   {:color :blue
    :font-size 20}])
```

You are able to combine both of these syntaxes.

```clojure
(defmedia --responsive-header
  ["@media(min-width: 420px)"
   "@media(min-width: 920px)"]
  {:display :block
   :width   ["50%" "75%"]}
  [{:color :green
    :font-size 16}
   {:color :blue
    :font-size 20}])
```

Output of it will be:

```css
{
    display: block;

    '@media(min-width: 420px)': {
        color: green;
        font-size: 16px;
        width: 50%;
    }

    '@media(min-width: 920px)': {
        color: green;
        font-size: 20px;
        width: 75%;
    }
}
```

If you prefer using the first element of the list as css properties without media queries, you need
to pass `nil` as the first element of vector.

```clojure
(defmedia --responsive-header
  [nil
   "@media(min-width: 420px)"
   "@media(min-width: 920px)"]
  {:width ["25%" "50%" "75%"]}
  [{:color :red
    :font-size 12}
   {:color :green
    :font-size 16}
   {:color :blue
    :font-size 20}])
```

Output of it will be:

```css
{
    width: 25%;
    color: red;
    font-size: 12px;

    '@media(min-width: 420px)': {
        color: green;
        font-size: 16px;
        width: 50%;
    }

    '@media(min-width: 920px)': {
        color: green;
        font-size: 20px;
        width: 75%;
    }
}
```

### `defwithc`
With the help of this macro you are able to change tag/component of styled component. Sometimes you want to create
some styles with one component but then use those styles again with another component.
`defwithc` method can be used in this case.

https://emotion.sh/docs/styled#change-the-rendered-tag-using-withcomponent
```clojure
(defstyled Header :h1
  {:font-size 14})

(defwithc Subheader Header :h2)
```


### `with-component`
This function is almost the same as `defwithc` but instead of defining new var this function returns
new styled component with replaced tag/component. This function can be pretty useful when you need
to change tag/component in runtime, for example based on some property.

```clojure
(defstyled Header :h1
  {:font-size 14})

(with-component Header :div)
```


### `Global`
Sometimes you might want to insert global css like resets or font faces. You can use the `Global` macro to do this.

https://emotion.sh/docs/globals

Example with helix:
```clojure
($ Global reset-styles global-styles)
```


### Passing className
By default `emption-cljs` allows to use the following className props: `:class-name`, `:class` or `:className`. These
props will be converted to `:class-name` when passing into React component, and converted to `:className`
when passing to tag component.
If you need to pass `:className` prop to React component, you should use `:class-name-prop` option. 
For example you need to pass `:className` prop when using native React component.
`:class-name-prop` allows next values: `:class-name` (default), `:class` or `:className`. 

Example with material-ui:
```clojure
[emotion.core :refer [defstyled]]
["@material-ui/core/Button" :default muiButton]
...
(defstyled Button
  [muiButton {:class-name-prop :className}]
  {:font-size 14})
```

If you need to change default className prop, custom `defstyled` macro should be created.

For example:
```clojure
(defmacro defstyled [sym component & styles]
  (let [[component
         options] (if (sequential? component) component [component])
        options   (assoc options :class-name-prop :className)]
    `(emotion-cljs.core/defstyled ~sym [~component ~options]
       ~@styles)))
```


### Convert props to camelCase
By default `emotion-cljs` converts kebab-case props to camelCase when passing them to tag component.
For example, `:auto-focus`, `:on-click`, `:class-name` props will be converted into `:autoFocus`, `:onClick`, `:className`.
If you want to disable this behaviour and use camelCase props in your components, you are able to do it by passing
`:camel-casing-props? false` option.

For example:
```clojure
[emotion.core :refer [defstyled]]
["@material-ui/core/Button" :default muiButton]
...
(defstyled Button
  [muiButton {:camel-casing-props? false}]
  {:font-size 14})
```

If you need to disable camel casing props by default, you should create custom `defstyled` macro described above.

Also after disabling camel casing props, `emotion-cljs` will become a little bit faster, because there is no need in
additional transformations and components wrapping.


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

## Performance
To check the performance of emotion-cljs compared to native emotion-js, you can run npm test. The current results of rendering styles 100,000 times are as follows (Apple M1 Max):
``` clojure
Native: 1425.1221660375595
EmotionCljs: 1704.9198750257492
```

## Roadmap
* Targeting another emotion component. https://emotion.sh/docs/styled#targeting-another-emotion-component
* Server-side rendering. PR is welcome!


## License
Copyright © 2020-2023 Yuri Khmelevsky 🇺🇦

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
