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
