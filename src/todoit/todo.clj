(ns todoit.todo
  (:require [io.pedestal.interceptor :refer [defhandler]]
            [io.pedestal.http.route :refer [url-for]]
            [ring.util.response :refer [response redirect]]
            [datomic.api :as d]
            [todoit.todo.db :as db]
            [todoit.todo.view :as v]))

(defhandler index [req]
  (let [todos (db/all-todos (d/db db/conn))]
    (response (v/todo-index todos))))

(defhandler create [req]
  (let [title (get-in req [:form-params "title"])
        desc (get-in req [:form-params "description"])]
    (when title
      (db/create-todo title desc))
    (redirect (url-for :todos))))

(defn id [request]
  (some-> request
          (get-in [:path-params :id])
          Long/parseLong))

(defhandler delete [req]
  (when-let [id (id req)]
    (db/delete-todo id))
  (redirect (url-for :todos)))

(defhandler toggle [req]
  (let [id (id req)
        status (some-> req
                       (get-in [:form-params "status"])
                       Boolean/parseBoolean)]
    (when (and id
               (some? status))
      (db/toggle-status id status)))
  (redirect (url-for :todos)))

(defhandler delete-all [req]
  (let [todos (db/all-todos (d/db db/conn))
        ids (map :db/id todos)]
    (doall
     (map db/delete-todo ids)))
  (redirect (url-for :todos)))
