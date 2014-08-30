(ns bmp.core
  (:use bmp.file-format bmp.resample bmp.rotate bmp.translate bmp.mirror bmp.rectangle)
  (:require [clojure.tools.cli :refer [parse-opts]] [clojure.string :as str])
  (:gen-class))

(def cli-options
  ;; An option with a required argument
  [["-i" "--in filename.bmp" "Input file (required)"
    :default nil
    :validate [#(.exists (java.io.File. %)) "Input file must exist"]]
   ["-o" "--out filename.bmp" "Output file (required)"
    :default nil]
   ["-s" "--scale WxH" "Scale image to WxH pixels"
    :parse-fn (fn [param] (map #(Integer. %) (str/split param #"x")))
    :validate [#(and (> (nth % 0) 0) (> (nth % 1) 0)) "Width and height must be positive"]]
   ["-t" "--translate X,Y" "Translate image by X pixels horizontally and Y vertically (positive is down)"
    :parse-fn (fn [param] (map #(Integer. %) (str/split param #",")))]
   [nil "--mirror-x" "Mirror image horizontally"]
   [nil "--mirror-y" "Mirror image vertically"]
   ["-r" "--rotate A" "Rotate image by A degrees; A must be a multiple of 90"
    :parse-fn #(Integer. %)]
   ["-f" "--fill X1,Y1,X2,Y2,R,G,B" "Fill rectangle with a color"
    :parse-fn (fn [param] (map #(Integer. %) (str/split param #",")))]])

(def transforms {:scale #(resample %1 %2)
                 :translate #(translate %1 %2)
                 :mirror-x #(mirror-x %2)
                 :mirror-y #(mirror-y %2)
                 :rotate #(rotate %1 %2)
                 :fill (fn [[x1 y1 x2 y2 r g b] bitmap] (fill-rectangle [b g r 255] [x1 y1 x2 y2] bitmap))})


(defn process-file [options]
  (let [bitmap (read-file (:in options))
        transformed-bitmap (reduce #(if (some? ((first %2) options))
                                      ((second %2) ((first %2) options) %1)
                                      %1)
                                   bitmap
                                   transforms)]
    (write-file (:out options) transformed-bitmap)
    )
  )

(defn -main
  [& args]
  (let [options (parse-opts args cli-options)]
    (if (and (some? (get-in options [:options :in])) (some? (get-in options [:options :out])))
      (if (:errors options)
        (do (println (:errors options)) (System/exit 1))
        (process-file (:options options)))
      (do (println "In and out files are required") (println (:summary options)) (System/exit 1))
      )))

