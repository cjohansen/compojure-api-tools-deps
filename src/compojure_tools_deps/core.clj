(ns compojure-tools-deps.core
  (:require [compojure.api.sweet :refer :all]
            [ring.adapter.jetty :as jetty]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))

(s/defschema Pizza
  {:name s/Str
   (s/optional-key :description) s/Str
   :size (s/enum :L :M :S)
   :origin {:country (s/enum :FI :PO)
            :city s/Str}})

(def app
  (api
   {:swagger
    {:ui "/"
     :spec "/swagger.json"
     :data {:info {:title "Simple"
                   :description "Compojure Api example"}
            :tags [{:name "api", :description "some apis"}]}}}

   (context "/api" []
            :tags ["api"]

            (GET "/plus" []
                 :return {:result Long}
                 :query-params [x :- Long, y :- Long]
                 :summary "adds two numbers together"
                 (ok {:result (+ x y)}))

            (POST "/echo" []
                  :return Pizza
                  :body [pizza Pizza]
                  :summary "echoes a Pizza"
                  (ok pizza)))))

(defn -main [& args]
  (jetty/run-jetty app {:port 8888 :join? false}))
