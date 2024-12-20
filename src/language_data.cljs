(ns language-data
  (:require ["@codemirror/language-data" :as ld :refer [languages]]))

;; oddly enough can't shadow the referred export with the same var name
(def ls languages)

(defn ^:dev/after-load boot []
  (js/console.log :boot ld))
