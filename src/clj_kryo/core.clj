(ns clj-kryo.core
  "Clojure library for the Kryo serialization/deserialization API."
  (:require
   [clojure.string :as str]
   [clojure.java.io :as jio])
  (:import
   [java.io File OutputStream InputStream FileOutputStream FileInputStream]
   [java.util UUID]
   [clojure.lang Keyword Symbol PersistentVector PersistentList PersistentHashSet
    PersistentHashMap PersistentArrayMap LazySeq]
   [com.esotericsoftware.kryo Kryo Serializer]
   [com.esotericsoftware.kryo.io Output Input]
   [clj_kryo.support KryoWrapper KryoSerializer]))

(defn make-uuid-serializer []
  (proxy [Serializer] []
    (write [kryo ^Output output object]
      (.writeLong output (.getMostSignificantBits object) false)
      (.writeLong output (.getLeastSignificantBits object) false))
    (read [kryo ^Input input klass]
      (UUID. (.readLong input false) (.readLong input false)))))

(defn- make-clojure-reader-serializer []
  (proxy [Serializer] []
    (write [kryo ^Output output object]
      (.writeString output (pr-str object)))
    (read [kryo ^Input input klass]
      (read
       (java.io.PushbackReader.
        (jio/reader (java.io.StringReader. (.readString input))))))))

(defn- make-clojure-symbol-serializer []
  (proxy [Serializer] []
    (write [kryo ^Output output object]
      (.writeString output (name object)))
    (read [kryo ^Input input klass]
      (clojure.lang.Symbol/intern (.readString input)))))

(defn- make-clojure-keyword-serializer []
  (proxy [Serializer] []
    (write [kryo ^Output output object]
      (.writeString output (str (.-sym object))))
    (read [kryo ^Input input klass]
      (clojure.lang.Keyword/intern (.readString input)))))

(defn- make-clojure-coll-serializer [init-coll]
  (proxy [Serializer] []
    (write [^Kryo kryo ^Output output coll]
      (.writeInt output (count coll))
      (doseq [el coll] (.writeClassAndObject kryo output el)))
    (read [^Kryo kryo ^Input input klass]
      (loop [n (.readInt input)
             coll init-coll]
        (if (< 0 n)
          (recur (- n 1) (conj coll (.readClassAndObject kryo input)))
          coll)))))

(defn- write-map
  [^Kryo kryo ^Output output m]
  (.writeInt output (count m))
  (doseq [[k v] m]
    (.writeClassAndObject kryo output k)
    (.writeClassAndObject kryo output v)))

(defn- read-map
  [^Kryo kryo ^Input input]
  (doall
   (loop [remaining (.readInt input)
          data (transient {})]
     (if (zero? remaining)
       (persistent! data)
       (recur (dec remaining)
              (let [k (.readClassAndObject kryo input)
                    v (.readClassAndObject kryo input)]
                (assoc! data k v)))))))

(defn- make-clojure-map-serializer [init-map]
  (proxy [Serializer] []
    (write [^Kryo kryo ^Output output m]
      (write-map kryo output m))
    (read [^Kryo kryo ^Input input klass]
      (read-map kryo input))))

(defn make-kryo ^Kryo []
  (let [k ^Kryo (new Kryo)]
    (doseq [[^Class c s] {Keyword (make-clojure-keyword-serializer)
                          Symbol (make-clojure-symbol-serializer)
                          UUID (make-uuid-serializer)
                          PersistentVector (make-clojure-coll-serializer [])
                          PersistentList (make-clojure-coll-serializer '())
                          PersistentHashSet (make-clojure-coll-serializer #{})
                          PersistentHashMap (make-clojure-map-serializer {})
                          PersistentArrayMap (make-clojure-map-serializer {})
                          LazySeq (make-clojure-coll-serializer [])}]
      (.register k ^Class c ^Serializer s))
    (.setReferences k false)
    k))

(def ^:private clojure-kryo
  (let [kryo (make-kryo)]
    (KryoSerializer/setKryo kryo)
    kryo))

(defmulti make-input class)
(defmethod make-input String ^Input [^String path] (make-input (jio/file path)))
(defmethod make-input File ^Input [^File f] (make-input (FileInputStream. f)))
(defmethod make-input InputStream ^Input [^InputStream fs] (Input. fs))
(defmethod make-input Input ^Input [^Input in] in)

(defmulti make-output class)
(defmethod make-output String ^Output [^String path] (make-output (jio/file path)))
(defmethod make-output File ^Output [^File f] (make-output (FileOutputStream. f)))
(defmethod make-output OutputStream ^Output [^OutputStream fs] (Output. fs))
(defmethod make-output Output ^Output [^Output out] out)

(defn read-object [^Input input]
  (.readClassAndObject ^Kryo clojure-kryo input))

(defn write-object [^Output output object]
  (.writeClassAndObject ^Kryo clojure-kryo output object))

(defn object-seq [^Input input]
  (when-let [obj (read-object input)]
    (cons obj (lazy-seq (object-seq input)))))

(defn wrap-kryo-serializable [object]
  (KryoWrapper. object))

(defn serialize [clj-data]
  (KryoSerializer/write clj-data))

(defn deserialize [clj-data-bin]
  (KryoSerializer/read clj-data-bin))
