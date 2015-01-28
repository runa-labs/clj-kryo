(defproject org.clojars.runa/clj-kryo "1.5.0"
  :description "Clojure library for the Kryo serialization API."
  :url "https://github.com/runa-labs/clj-kryo"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.esotericsoftware/kryo "3.0.0"]]
  :java-source-paths ["src/java"]
  :aot [clj-kryo.core])
