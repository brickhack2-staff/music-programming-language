(ns mpl.core
  (:require [mpl.ast :as ast]
            [mpl.interpreter :as interpreter]
            [mpl.parse :as parse]
            [clojure.pprint :refer [pprint]])
  (:import [org.jfugue Player Tempo])
  (:gen-class))

; Placeholder for actual reading of source code from file.
(def source
"a:b
c:d
|a b c d|e f g h|")


(defn error-msg
  "Formats cli parse error message."
  [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (clojure.string/join \newline errors)))

(defn exit
  "Exits the program, with an optional exit status and message.
  Default exit status is 0."
  ([]
     (System/exit 0))
  ([status]
     (System/exit status))
  ([status msg]
     (println msg)
     (System/exit status)))

(defn errors-occured [n]
  (str n " error"
       (if (= 1 n) "" "s")
       " occurred."))

(defn -main
  [source-file & args]
  (let [[ast parser errors] (parse/parse-source source-file)
        ;; Exit if syntax errors occured.
        _ (when (pos? errors)
            (exit 1 (errors-occured errors)))
        ;; make ast nicer
        ast (ast/ast ast)
        ;; parse tempo
        tempo (-> ast
                  :meta
                  :tempo)
        tempo (if tempo
                (Integer/parseInt tempo)
                220)
        tempo (new Tempo tempo)
        ;; init player
        player (new Player)]
    ;; Run program.
    (interpreter/interpret (:body ast)
                           (interpreter/empty-tape)
                           player
                           tempo)))
