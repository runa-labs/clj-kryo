# Clj-kryo

Clojure wrapper for Kryo, a fast and efficient object graph serialization framework for Java.


## Leiningen

```clj
  [org.clojars.runa/clj-kryo "1.5.0"]
```

## Usage

May be best explained by following example:

```clj
(require '[clj-kryo.core :as kryo])

(defn kryo-round-trip [expr]
  (let [bos (ByteArrayOutputStream.)]
    (with-open [out ^Output (kryo/make-output bos)]
      (kryo/write-object out expr))
    (let [bis (ByteArrayInputStream. (.toByteArray bos))]
      (with-open [in ^Input (kryo/make-input bis)]
        (kryo/read-object in)))))
```

It includes both serializing and deserializing of clojure expression. 
Methods of interest here are namely following:

* `make-output`
* `make-input`
* `read-object`
* `write-object`

Both make-output and make-input are multi-methods that currently supports
turning java `String`, `File`, `OutputStream` and `InputStream` respectively into
kryo Input and Output where serialized and deserialized clojure data 
structures can be persisted.

`read-object` and `write-object` uses registered serializers, which currently
supports all clojure data structures on top of kyro supported Java classes,
to perform serialization and deserialization respectively.

Above should be enough to use this library for most of projects but if you
have custom Java class, or clojure datastructure, you can register your 
custom serializer by modifying make-kryo fn (future version might support 
by just implementing a single method). For example, look at the
make-clojure-map-serializer fn in core.clj file.

Following are example of how to use it:

```clj
(let [bos (java.io.ByteArrayOutputStream.)]
           (with-open [out ^Output (kryo/make-output bos)]
             (kryo/write-object out (java.util.HashMap. {:foo 1 :bar 2})))
           (.size bos))
```

## License

 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 *   the terms of this license.
 *   You must not remove this notice, or any other, from this software.
