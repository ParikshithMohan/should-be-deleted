(ns webtest.server
  (:require [websocket-example.handler :refer [app]]
            [environ.core :refer [env]]
            [org.httpkit.server :refer [run-server]])
  (:gen-class))

(defn -main [& args] ;;initializing the port number...converting from string to integer
  (let [port (Integer/parseInt (or (env :port) "3449"))]
    (run-server app {:port port :join? false})))

