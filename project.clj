(defproject net.clojars.karlthorssen/leipzig "0.11.1-alpha1"
  :description "A composition library for Clojure and Clojurescript."
  :url "http://github.com/ctford/leipzig"
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"
            :distribution :repo
            :comments "Same as Overtone"}
  :dependencies	[[org.clojure/clojure "1.10.3"]
                 [org.clojure/clojurescript "1.7.170"]
                 [overtone/at-at "1.2.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]]
  :source-paths ["src/clj" "src/cljc"]
  :profiles {:dev
             {:plugins [[lein-midje "3.2.1"]
                        [codox "0.8.8"]]
              :dependencies  [[midje "1.9.9"] ]}}
  :cljsbuild {:builds {:prod {:source-paths ["src/cljc"]
                              :compiler     {:output-to     "target/cljs/leipzig.js"
                                             :optimizations :whitespace}}}}
  :codox {:src-dir-uri "http://github.com/ctford/leipzig/blob/0.9.0/"})
