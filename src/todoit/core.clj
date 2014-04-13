(ns todoit.core
  (:require [io.pedestal.http.route.definition :refer [defroutes]]
            [io.pedestal.http.route :as route :refer [router]]
            [io.pedestal.http.body-params :refer [body-params]]
            [io.pedestal.http.ring-middlewares :as middleware]
            [io.pedestal.http :as http]
            [io.pedestal.interceptor :refer [defon-request defon-response]]
            [ns-tracker.core :refer [ns-tracker]]
            [ring.handler.dump :refer [handle-dump]]
            [todoit.todo :as todo]))

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
  [[["/" ^:interceptors [http/html-body
                         (body-params)
                         affix-custom-server]
     ["/hello" ^:interceptors [capitalize-name] {:get hello-world}]
     ["/goodbye" {:get goodbye-cruel-world}]
     ["/request" {:any handle-dump}]
     ["/todos" {:get [:todos todo/index]
                :post [:todos#create todo/create]}]]]])

(def modified-namespaces (ns-tracker "src"))

(def service
  {::http/interceptors [http/log-request
                        http/not-found
                        route/query-params
                        (middleware/file-info)
                        (middleware/resource "public")
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
