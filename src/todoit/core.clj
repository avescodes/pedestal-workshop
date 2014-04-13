(ns todoit.core
  (:require [io.pedestal.http.route.definition :refer [defroutes]]
            [io.pedestal.http.route :as route :refer [router]]
            [io.pedestal.http :as http]
            [io.pedestal.interceptor :refer [defon-request defon-response]]
            [ns-tracker.core :refer [ns-tracker]]
            [ring.handler.dump :refer [handle-dump]]))

(defn hello-world [req]
  (let [name (get-in req [:query-params :name])]
    {:status 200
     :body (str "Hello, " (or name "World") "!")
     :headers {}}))

(defon-request capitalize-name [req]
  (update-in req [:query-params :name] (fn [name] (when name
                                                    (clojure.string/capitalize name)))))

(defon-response affix-custom-server [resp]
  (update-in resp [:headers "Server"] (constantly "Computron 9000")))

(defn goodbye-cruel-world [req]
  {:status 200
   :body "Goodbye, cruel world!"
   :headers {}})

(defroutes routes
  [[["/" ^:interceptors [affix-custom-server]
     ["/hello" ^:interceptors [capitalize-name] {:get hello-world}]
     ["/goodbye" {:get goodbye-cruel-world}]
     ["/request" {:any handle-dump}]]]])

(def modified-namespaces (ns-tracker "src"))

(def service
  {::http/interceptors [http/log-request
                        http/not-found
                        route/query-params
                        (router (fn []
                                  (doseq [ns-sym (modified-namespaces)]
                                    (require ns-sym :reload))
                                  routes))]
   ::http/join? false
   ::http/port (or (some-> (System/getenv "PORT")
                           Integer/parseInt)
                   8080)})

(defn -main [& args]
  (-> service
      http/create-server
      http/start))
