# presioke

This ClojureScript application uses Hoplon and the flickr API to
obtain random images and display them in the browser.  [Try it](http://alandipert.github.io/presioke/) or [see all of the code](https://github.com/tailrecursion/presioke/blob/master/src/html/index.cljs).

Hoplon (née HLisp) is a Leiningen plugin containing an extended
ClojureScript compiler and related tools and libraries like
[Javelin](https://github.com/tailrecursion/javelin).

For a rationale and some context related to this style of application
development, please see [this design
document](https://github.com/tailrecursion/hlisp-starter/blob/master/PROJECT.md).

Hoplon is currently under heavy development, and this demo is subject
to frequent breaking change.  That said, we welcome feedback and
contribution.

## Usage

    lein hoplon auto  # compile ClojureScript (and automatically if sources change)

Then, open `resources/public/index.html` in your browser.

## License

Copyright © 2013 Alan Dipert

Distributed under the Eclipse Public License, the same as Clojure.
