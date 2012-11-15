(defproject com.runa/clj-kryo "1.2.0-SNAPSHOT"
  :description "Clojure library for the Kryo serialization API."
  :plugins [[s3-wagon-private "1.1.2"]
            [lein-swank "1.4.4"]]
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.esotericsoftware.kryo/kryo "2.20"]]
  :repositories {"releases" {:url "s3p://runa-maven/releases/"}
                 "snapshots" {:url "s3p://runa-maven/snapshots/"}}
  :java-source-path "src/java"
  :aot [clj-kryo.core])
