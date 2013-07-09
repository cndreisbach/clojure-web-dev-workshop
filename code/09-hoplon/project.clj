(defproject tailrecursion/presioke "0.1.0-SNAPSHOT"
  :description "Presentation Karaoke with Clojure and ClojureScript"
  :url "https://github.com/tailrecursion/presioke"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :eval-in :leiningen
  :plugins      [[tailrecursion/hoplon "0.1.0-SNAPSHOT"]]
  :dependencies [[org.clojure/clojure     "1.5.1"]
                 [alandipert/interpol8    "0.0.3"]
                 [tailrecursion/hoplon    "0.1.0-SNAPSHOT"]]
  :hoplon       {:cljsc-opts  {:pretty-print  false
                               :optimizations :advanced}})
