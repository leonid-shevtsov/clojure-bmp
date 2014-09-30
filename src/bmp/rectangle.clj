(ns bmp.rectangle)

(declare replace-middle)

(defn fill-rectangle
  "Flat fills given rectangle on a bitmap with a color"
  [color [x1 y1 x2 y2] bitmap]
  (assoc bitmap :pixels
    (replace-middle y1 y2 (partial replace-middle x1 x2 (constantly color)) (:pixels bitmap))))

(defn replace-middle [start end new-function coll]
  (let [[before not-before] (split-at start coll)
        [middle after] (split-at (- end start) not-before)]
    (concat before (map new-function middle) after)))
