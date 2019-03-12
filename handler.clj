(ns webtest.handler
  (:require
   [reitit.ring :as reitit-ring]
   [webtest.middleware :refer [middleware]]
   [hiccup.page :refer [include-js include-css html5]]
   [org.httpkit.server :refer [with-channel on-close send!] ;;<--added
   [config.core :refer [env]]))

  (def home-page  ;; initializing the html page...like setting the width...its like                             the <head> of html
    (html
     [:html
      [:head
       [:meta {:charset "utf-8"}]
       [:meta {:name "viewport"
               :content "width=device-width, initial-scale=1"}]
       (include-css (if (env :dev) "css/site.css" "css/site.min.css"))]
      [:body
       [:div#app]
       (include-js "js/app.js")]])) ;; includin a javascript folder

  ;; --> Added
  (def clients (atom {})) ;; websocket congiguration...checks the status of connection...server connected or disconnected with the client
  (defn ws
    [req]
    (with-channel req con
      (swap! clients assoc con true)
      (println con " connected")
      (on-close con (fn [status]
                      (swap! clients dissoc con)
                      (println con " disconnected. status: " status)))))

  (defn write-message [message] ;;to check whether a message is sent/recieved...and send back a message
    (doseq [client @clients]
      (send! (key client) message false)))
  ;; <--

  (defroutes routes   ;;setting a route for the client to communicate with the server
    (GET "/" [] home-page)
    (GET "/message" [] ws) ;; -->Added
    (resources "/")
    (not-found "Not Found"))

  (def app
    (let [handler (wrap-defaults routes site-defaults)]
      (if (env :dev) (wrap-exceptions handler) handler)))
