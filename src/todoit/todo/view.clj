(ns todoit.todo.view
  (:require [io.pedestal.http.route :refer [url-for]]
            [hiccup.page :refer [html5]]
            [hiccup.core :refer [html h]]
            [hiccup.form :as f]))

(defn todo-form [] ;; Later, this could take an existing todo...
  [:form.form-horizontal
   {:action (url-for :todos#create)
    :method :post}
   [:p.lead "Add TODO"]
   [:div.form-group
    [:label.control-label.col-sm-2 {:for "title-input"} "Title"]
    [:div.col-sm-10
     [:input.form-control {:type "text"
                           :name "title"
                           :id "title-input"
                           :placeholder "I need to..."
                           :required true}]]]
   [:div.form-group
    [:label.control-label.col-sm-2 {:for "description-input"} "Description"]
    [:div.col-sm-10
     [:input.form-control {:type "text"
                           :name "description"
                           :id "description-input"
                           :placeholder "because..."}]]]
   [:div.form-group
    [:div.col-sm-offset-2.col-sm-10
     [:button.btn.btn-default {:type "submit"} "Create"]]]])

(defn todo-index [todos]
  (html5 {:lang "en"}
         [:head
          [:title "My Todos"]
          [:meta {:name :viewport
                  :content "width=device-width, initial-scale=1"}]
          [:link {:href "/bootstrap/css/bootstrap.min.css"
                  :rel "stylesheet"}]]
         [:body
          [:div.container
           [:h1 "My Todos"]
           [:div.row
            (if (seq todos)
              [:table.table.table-striped
               [:thead
                [:tr
                 [:th "Title"]
                 [:th "Description"]]]
               [:tbody
                (for [todo todos]
                  [:tr
                   [:td (:todo/title todo)]
                   [:td (:todo/description todo)]])]]
              [:p "All done!"])]
           (todo-form)]
          [:script {:src "http://code.jquery.com/jquery-2.1.0.min.js"}]
          [:script {:src "/bootstrap/js/bootstrap.min.js"}]]))
