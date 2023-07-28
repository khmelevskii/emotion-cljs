# Changelog

## 0.2.1

### Changed

- By default, in development mode, the namespace is appended to class names.

## 0.2.0

[BREAKING CHANGES]

Optimize performance of almost all function, some code has been transferred from runtime to macro.

### Added

- Add `css` function for styles within conditions

### Fixed

- Remove `let-css` function

## 0.1.5

### Added

- Add `:wrap` option to `defstyled` macro
- Add example with `emotion-cljs` and `reagent` ([#5](https://github.com/khmelevskii/emotion-cljs/issues/5))

### Fixed

- Add `cljs-bean` dependency ([#4](https://github.com/khmelevskii/emotion-cljs/issues/4))

## 0.1.4

### Added

- Add `defmedia` macro for working with media queries
- Add `with-component` function to change tag of styled component in runtime

### Fixed

- Fix `displayName` of styled components
- Fix setting additional `class-name` in case when component has `class-name`
- Use `forwardRef` in `defwithc`
