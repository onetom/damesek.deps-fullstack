(ns user
  (:require
    [clojure.tools.namespace.repl :as ns-tools]
    [mount.core :as mount]
    [re-frame.core :as rf]
    ;[project.clj.db.persons :refer [persons-init-db]]
    [project.clj.components.service :refer [http-server]]
    [project.clj.components.config :refer [conf]]
    [frontend :refer [shadow-cljs-server cljs-app-watcher]]
    [backend :refer [css-watcher]]
    [project.clj.components.db :refer [*xtdb*]]))


(ns-tools/set-refresh-dirs "src/main/")


; **** Reloaded Workflow ****
; Start components won't be reloaded on (reset)

(def start-components [#'css-watcher
                       #'http-server
                       #'*xtdb*
                       #'shadow-cljs-server
                       #'cljs-app-watcher
                       #'conf])

; Reset components are stopped and started between code reloading

(def reset-components [#'http-server
                       #'shadow-cljs-server
                       #'cljs-app-watcher])


; State management

(defn start []
  (rf/clear-subscription-cache!)
  (doseq [compoments (:started (mount/start start-components))]
    (println compoments " started"))
  :ready)

(defn restart []
  (doseq [compoments (:started (mount/start reset-components))]
    (println compoments " started"))
  :ready)

(defn stop []
  (doseq [compoments (:stopped (mount/stop reset-components))]
    (Thread/sleep 500)
    (println compoments " stopped"))
  :stop-done)


(defn re []                                                 ;<- re-mount the things
  (stop)
  (rf/clear-subscription-cache!)
  (ns-tools/refresh :after 'user/restart)
  :ready)


(defn stop-all []                                           ; <-- just for tests
  (doseq [compoments (:stopped (mount/stop start-components))]
    (Thread/sleep 500)
    (println compoments " stopped"))
  :stop-all-done)


(comment
  ;
  ;  (start)
  ;  (stop-all)
  ;  (re)
  ;  (mount/running-states)

  ; run just once:
   ;(persons-init-db *xtdb*)
  ;(project.clj.db.persons/import-docs {:db *xtdb* :docs (project.clj.db.persons/xt-person)})
  )