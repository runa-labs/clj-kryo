(defproject com.runa/clj-kryo "1.0.0"
  :description "Clojure library for the Kryo serialization API."
  :plugins [[s3-wagon-private "1.1.2"]]
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [com.esotericsoftware.kryo/kryo "2.20"]]
  :repositories {"releases" {:url "s3p://runa-maven/releases/"}}
  :aot [clj-kryo.core])
