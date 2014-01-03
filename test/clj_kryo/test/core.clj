(ns clj-kryo.test.core
  (:use clojure.test)
  (:require
   [clj-kryo.core :as kryo])
  (:import
   java.util.HashMap
   [java.io ByteArrayInputStream ByteArrayOutputStream]
   [com.esotericsoftware.kryo.io Output Input]
   clj_kryo.support.KryoSerializer))

(deftest write-object
  (is (< 0
         (let [bos (ByteArrayOutputStream.)]
           (with-open [out ^Output (kryo/make-output bos)]
             (kryo/write-object out (HashMap. {:foo 1 :bar 2})))
           (.size bos)))))

(defn kryo-round-trip [expr]
  (-> expr
      kryo/serialize
      kryo/deserialize))

(deftest read-object
  (is (= 1 (kryo-round-trip 1)))
  (is (= "abc" (kryo-round-trip "abc")))
  (is (= 'abc (kryo-round-trip 'abc)))
  (is (= :abc (kryo-round-trip :abc)))
  (is (= #uuid "2ee8cfbc-44e3-4452-b638-f099a6d3319e"
         (kryo-round-trip #uuid "2ee8cfbc-44e3-4452-b638-f099a6d3319e")))
  (is (= ["foo"] (kryo-round-trip ["foo"])))
  (is (= '("foo") (kryo-round-trip '("foo"))))
  (is (= #{"foo"} (kryo-round-trip #{"foo"})))
  (is (= #{"foo" 1} (kryo-round-trip #{"foo" 1})))
  (is (= ["foo"] (kryo-round-trip (lazy-seq ["foo"]))))

  (let [m {:foo 1 :bar [2 3] :baz "four"}]
    (is (= m (kryo-round-trip m))))

  (let [m {32  {"int" 1 "string" "string"}
           65 {"foo" "bar"}}]
    (is (= m
           (let [bos (ByteArrayOutputStream.)]
             (with-open [out ^Output (kryo/make-output bos)]
               (kryo/write-object out m))
             (let [bis (ByteArrayInputStream. (.toByteArray bos))]
               (with-open [in ^Input (kryo/make-input bis)]
                 (into {} (kryo/read-object in)))))))))

(deftest kryo-serializer
  (let [m {:a 1}]
    (is (= m (KryoSerializer/read (KryoSerializer/write m))))))


(def example-session [{:data {:url "http://whatever.com?gclid=adwords&b=blah"},
             :event-id ::varying-field,
             :event-type :path,
             :git-sha "",
             :merchant-id "c4fdad7d-9139-fbb2-c78a-9597fcef487b",
             :request-id ::varying-field,
             :runa-cid ::varying-field,
             :runa-enabled true,
             :session-id ::varying-field,
             :site-cid "99999999-9999-9999-9999-999999999999",
             :timestamp ::varying-field,
             :version 1}
            {:data
             {:baseline-info
              {:enabled true,
               :offer false,
               :percentage 100.0,
               :starts-on 1356036720000},
              :bucket ::varying-field,
              :client-info
              {:browser {:name "Unknown", :rendering-engine "OTHER"},
               :device {:mobile? false, :type "Unknown"},
               :ip-address ::varying-field,
               :os {:name "Unknown"},
               :runa-cid ::varying-field},
              :consumer nil,
              :geo-data
              {:ipinfo
               {:Location
                {:CityData {:postal_code 94536},
                 :CountryData {:country_code "us"},
                 :StateData {:state_code "ca"}},
                :ip_address ::varying-field}},
              :landing-url "http://whatever.com?gclid=adwords&b=blah",
              :merchant nil,
              :offer-model :random,
              :paid-ad false,
              :paid-search-terms ["president" "solitaire"],
              :rmc-cookies nil,
              :search-terms ["president" "solitaire"],
              :session-start-time ::varying-field,
              :sushi nil,
              :url-referrer
              "http://www.google.com/url?sa=t&source=web&cd=1&ved=0CBsQFjAA&url=http://www.presidentsolitaire.com/&rct=j&q=president solitaire&ei=vGGQTNjiEoj2tgOKp8SxDg&usg=AFQjCNEgO94CXSKxjr7vRxT5R4dQXu_XLQ"},
             :event-id ::varying-field,
             :event-type :session-data,
             :git-sha "",
             :merchant-id "c4fdad7d-9139-fbb2-c78a-9597fcef487b",
             :request-id ::varying-field,
             :runa-cid ::varying-field,
             :runa-enabled true,
             :session-id ::varying-field,
             :site-cid "99999999-9999-9999-9999-999999999999",
             :timestamp ::varying-field,
             :version 1}
            {:data
             {:page-type :prod,
              :products
              [{:categories ["Mens" "Boots" "Pull Ups"],
                :list-price-cents 500,
                :location nil,
                :merchant-product-id "ipad",
                :name "Gummi Berries",
                :original-price-cents 500,
                :quantity 1,
                :runa-price-cents nil,
                :runa-product-id "ipad",
                :unit-price-cents 500}],
              :request-url "http://whatever.com?gclid=adwords&b=blah"},
             :event-id ::varying-field,
             :event-type :view,
             :git-sha "",
             :merchant-id "c4fdad7d-9139-fbb2-c78a-9597fcef487b",
             :request-id ::varying-field,
             :runa-cid ::varying-field,
             :runa-enabled true,
             :session-id ::varying-field,
             :site-cid "99999999-9999-9999-9999-999999999999",
             :timestamp ::varying-field,
             :version 1}
            {:data
             {:delivery-method nil,
              :incentives [],
              :model-response nil,
              :model-used nil,
              :offer-model :random,
              :reason :no-applicable-promos,
              :type :not-determined},
             :event-id ::varying-field,
             :event-type :promo,
             :git-sha "",
             :merchant-id "c4fdad7d-9139-fbb2-c78a-9597fcef487b",
             :request-id ::varying-field,
             :runa-cid ::varying-field,
             :runa-enabled true,
             :session-id ::varying-field,
             :site-cid "99999999-9999-9999-9999-999999999999",
             :timestamp ::varying-field,
             :version 1}
            {:data
             {:delivery-method :pre-abandonment,
              :incentives [],
              :model-response nil,
              :model-used nil,
              :offer-model :random,
              :page-type :prod,
              :product-id->product+incentives nil,
              :type :locked},
             :event-id ::varying-field,
             :event-type :promo,
             :git-sha "",
             :merchant-id "c4fdad7d-9139-fbb2-c78a-9597fcef487b",
             :request-id ::varying-field,
             :runa-cid ::varying-field,
             :runa-enabled true,
             :session-id ::varying-field,
             :site-cid "99999999-9999-9999-9999-999999999999",
             :timestamp ::varying-field,
             :version 1}
            {:data {:url "http://miva-terra.runa.com/"},
             :event-id ::varying-field,
             :event-type :path,
             :git-sha "",
             :merchant-id "c4fdad7d-9139-fbb2-c78a-9597fcef487b",
             :request-id ::varying-field,
             :runa-cid ::varying-field,
             :runa-enabled true,
             :session-id ::varying-field,
             :site-cid "99999999-9999-9999-9999-999999999999",
             :timestamp ::varying-field,
             :version 1}
            {:data
             {:baseline-info
              {:enabled true,
               :offer false,
               :percentage 100.0,
               :starts-on 1356036720000},
              :bucket ::varying-field,
              :client-info
              {:browser {:name "Unknown", :rendering-engine "OTHER"},
               :device {:mobile? false, :type "Unknown"},
               :ip-address ::varying-field,
               :os {:name "Unknown"},
               :runa-cid
               ::varying-field},
              :consumer nil,
              :geo-data
              {:ipinfo
               {:Location
                {:CityData {:postal_code 94536},
                 :CountryData {:country_code "us"},
                 :StateData {:state_code "ca"}},
                :ip_address ::varying-field}},
              :landing-url "http://whatever.com?gclid=adwords&b=blah",
              :merchant nil,
              :offer-model :random,
              :paid-ad false,
              :paid-search-terms ["president" "solitaire"],
              :rmc-cookies nil,
              :search-terms ["president" "solitaire"],
              :session-start-time ::varying-field,
              :sushi nil,
              :url-referrer
              "http://www.google.com/url?sa=t&source=web&cd=1&ved=0CBsQFjAA&url=http://www.presidentsolitaire.com/&rct=j&q=president solitaire&ei=vGGQTNjiEoj2tgOKp8SxDg&usg=AFQjCNEgO94CXSKxjr7vRxT5R4dQXu_XLQ"},
             :event-id ::varying-field,
             :event-type :session-data,
             :git-sha "",
             :merchant-id "c4fdad7d-9139-fbb2-c78a-9597fcef487b",
             :request-id ::varying-field,
             :runa-cid ::varying-field,
             :runa-enabled true,
             :session-id ::varying-field,
             :site-cid "99999999-9999-9999-9999-999999999999",
             :timestamp ::varying-field,
             :version 1}
            {:data
             {:page-type :prod,
              :products
              [{:categories ["Mens" "Boots" "Pull Ups"],
                :list-price-cents 500,
                :location nil,
                :merchant-product-id "ipad",
                :name "Gummi Berries",
                :original-price-cents 500,
                :quantity 1,
                :runa-price-cents nil,
                :runa-product-id "ipad",
                :unit-price-cents 500}],
              :request-url "http://miva-terra.runa.com/"},
             :event-id ::varying-field,
             :event-type :view,
             :git-sha "",
             :merchant-id "c4fdad7d-9139-fbb2-c78a-9597fcef487b",
             :request-id ::varying-field,
             :runa-cid ::varying-field,
             :runa-enabled true,
             :session-id ::varying-field,
             :site-cid "99999999-9999-9999-9999-999999999999",
             :timestamp ::varying-field,
             :version 1}
            {:data
             {:delivery-method :pre-abandonment,
              :incentives [],
              :model-response nil,
              :model-used nil,
              :offer-model :random,
              :reason :no-applicable-promos,
              :type :not-determined},
             :event-id ::varying-field,
             :event-type :promo,
             :git-sha "",
             :merchant-id "c4fdad7d-9139-fbb2-c78a-9597fcef487b",
             :request-id ::varying-field,
             :runa-cid ::varying-field,
             :runa-enabled true,
             :session-id ::varying-field,
             :site-cid "99999999-9999-9999-9999-999999999999",
             :timestamp ::varying-field,
             :version 1}
            {:data
             {:cart
              {:basket-id nil,
               :charges [],
               :order-id nil,
               :products
               [{:attributes
                 [{:list-price 0, :name "Color", :price 0, :value "Red"}
                  {:list-price 300, :name "Size", :price 200, :value "XL"}],
                 :categories ["south_park" "robots"],
                 :list-price-cents 2400,
                 :merchant-product-id "MG200MMS",
                 :name "MegaMan 200",
                 :original-price-cents 2400,
                 :quantity 1,
                 :runa-price-cents 2200,
                 :runa-product-id "MG200MMS-color-red-size-xl",
                 :unit-price-cents 2200}],
               :shipping nil,
               :tax-cents 0},
              :coupon-code nil,
              :type :add},
             :event-id ::varying-field,
             :event-type :cart,
             :git-sha "",
             :merchant-id "c4fdad7d-9139-fbb2-c78a-9597fcef487b",
             :request-id ::varying-field,
             :runa-cid ::varying-field,
             :runa-enabled true,
             :session-id ::varying-field,
             :site-cid "99999999-9999-9999-9999-999999999999",
             :timestamp ::varying-field,
             :version 1}
            {:data
             {:cart
              {:basket-id "13bccc64e49ac61b5adf385f48c4104e",
               :charges [{:cost-cents -500, :type "COUPON"}],
               :order-id nil,
               :products
               [{:attributes
                 [{:list-price 0, :name "Color", :price 0, :value "Red"}
                  {:list-price 300, :name "Size", :price 200, :value "XL"}],
                 :categories ["south_park" "robots"],
                 :list-price-cents 2400,
                 :merchant-product-id "MG200MMS",
                 :name "MegaMan 200",
                 :original-price-cents 2400,
                 :quantity 10,
                 :runa-price-cents 2200,
                 :runa-product-id "MG200MMS-color-red-size-xl",
                 :unit-price-cents 2200}],
               :shipping nil,
               :tax-cents 0},
              :coupon-code nil,
              :type :show},
             :event-id ::varying-field,
             :event-type :cart,
             :git-sha "",
             :merchant-id "c4fdad7d-9139-fbb2-c78a-9597fcef487b",
             :request-id ::varying-field,
             :runa-cid ::varying-field,
             :runa-enabled true,
             :session-id ::varying-field,
             :site-cid "99999999-9999-9999-9999-999999999999",
             :timestamp ::varying-field,
             :version 1}
            {:data
             {:cart
              {:basket-id "13bccc64e49ac61b5adf385f48c4104e",
               :charges
               [{:cost-cents 500, :type "SHIPPING"}
                {:cost-cents 2295, :type "TAX"}
                {:cost-cents -500, :type "COUPON"}],
               :order-id "6589",
               :payment-type {:method "Banque Checque", :method-code "CHECK"},
               :products
               [{:attributes
                 [{:list-price 0, :name "Color", :price 0, :value "Red"}
                  {:list-price 300, :name "Size", :price 200, :value "XL"}],
                 :categories ["south_park" "robots"],
                 :list-price-cents 2400,
                 :merchant-product-id "MG200MMS",
                 :name "MegaMan 200",
                 :original-price-cents 2400,
                 :quantity 10,
                 :runa-price-cents 2200,
                 :runa-product-id "MG200MMS-color-red-size-xl",
                 :unit-price-cents 2200}],
               :runa-discount-total-cents -1234,
               :shipping
               {:cost-cents 500,
                :country "us",
                :method "5 Day Ground",
                :method-code "GROUND",
                :postal-code "94040",
                :state "ca"},
               :tax-cents 2295},
              :coupon-code nil,
              :type :purchase},
             :event-id ::varying-field,
             :event-type :cart,
             :git-sha "",
             :merchant-id "c4fdad7d-9139-fbb2-c78a-9597fcef487b",
             :request-id ::varying-field,
             :runa-cid ::varying-field,
             :runa-enabled true,
             :session-id ::varying-field,
             :site-cid "99999999-9999-9999-9999-999999999999",
             :timestamp ::varying-field,
             :version 1}])

(deftest example-session-test
  (testing "serialization/deserialation of example session"
    (is (= example-session)
        (-> example-session
            kryo/serialize
            kryo/deserialize))))
