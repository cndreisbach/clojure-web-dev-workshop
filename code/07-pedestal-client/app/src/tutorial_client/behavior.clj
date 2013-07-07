(ns ^:shared tutorial-client.behavior
    (:require [clojure.string :as string]
              [io.pedestal.app :as app]
              [io.pedestal.app.messages :as msg]))

(defn inc-transform [old-value _]
  ((fnil inc 0) old-value))

(defn init-main
  "Enable transformation of :my-counter by sending an :inc message.
  The default renderer will use this delta to create a button to
  send this message."
  [_]
  [[:transform-enable [:main :my-counter]
    :inc [{msg/topic [:my-counter]}]]])

(def example-app
  {:version 2

   ;; Send any :inc message with the path :my-counter to the
   ;; inc-transform function.
   :transform [[:inc   [:my-counter] inc-transform]]

   ;; Configure a sequence of emitters.
   ;; Emitters report change. This uses the default Pedestal
   ;; emitter to report changes, or _deltas_.
   
   :emit [;; This runs our custom emitter (init-main) when the
          ;; application starts.
          {:init init-main}
          ;; This default emitter sends all messages and prefixes
          ;; their path with :main.
          {:in #{[:*]} :fn (app/default-emitter [:main])}]})


;; (defn reset-transform [_ _]
;;   0)

;; (defn init-main
;;   "Enable transformation of :my-counter by sending an :inc message.
;;   The default renderer will use this delta to create a button to
;;   send this message."
;;   [_]
;;   [[:transform-enable [:main :my-counter]
;;     :inc [{msg/topic [:my-counter]}]]
;;    [:transform-enable [:main :my-counter]
;;     :reset [{msg/topic [:my-counter]}]]])

;; (def example-app
;;   {:version 2

;;    ;; Send any :inc message with the path :my-counter to the
;;    ;; inc-transform function.
;;    :transform [[:inc   [:my-counter] inc-transform]
;;                [:reset [:my-counter] reset-transform]]

;;    ;; Configure a sequence of emitters.
;;    ;; Emitters report change. This uses the default Pedestal
;;    ;; emitter to report changes, or _deltas_.
   
;;    :emit [;; This runs our custom emitter (init-main) when the
;;           ;; application starts.
;;           {:init init-main}
;;           ;; This default emitter sends all messages and prefixes
;;           ;; their path with :main.
;;           {:in #{[:*]} :fn (app/default-emitter [:main])}]})
