(defproject todoit "0.1.0-SNAPSHOT"
  :url "https://github.com/rkneufeld/pedestal-workshop"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [io.pedestal/pedestal.service "0.3.1"]
                 [io.pedestal/pedestal.service-tools "0.3.1"]
                 [io.pedestal/pedestal.jetty "0.3.1"]
                 [ns-tracker "0.2.2"]]
  :main todoit.core)
