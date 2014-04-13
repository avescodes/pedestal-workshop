(ns todoit.todo.db
  (:require [datomic.api :as d]))

(defonce uri (str "datomic:mem://" (gensym "todos")))
(d/create-database uri)
(def conn (d/connect uri))

(def schema-tx (->> "todos.edn"
                    clojure.java.io/resource
                    slurp
                    (clojure.edn/read-string {:readers *data-readers*})))

@(d/transact conn schema-tx)

(defn todo-tx [title desc]
  (cond-> {:db/id (d/tempid :db.part/user)
           :todo/title title
           :todo/completed? false}
          desc (assoc :todo/description desc) ;; Add description if not nil
          true vector))                       ;; Wrap in vector

(defn create-todo [title desc]
  @(d/transact conn (todo-tx title desc)))

(defn all-todos [db]
  (->> (d/q '[:find ?id
              :where [?id :todo/title]]
            db)                 ; #{[12341123] [12357223] [134571345]}
       (map first)              ; (12341123 12357223 134571345)
       (map #(d/entity db %)))) ; ({:db/id 12341123} ...)

(defn toggle-status [id status]
  @(d/transact conn [[:db/add id :todo/completed? status]]))

(defn delete-todo [id]
  @(d/transact conn [[:db.fn/retractEntity id]]))
