(defproject com.runa/clj-kryo "1.2.0"
  :description "Clojure library for the Kryo serialization API."
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.esotericsoftware.kryo/kryo "2.21"]]
  :java-source-paths ["src/java"]
  :aot [clj-kryo.core])
