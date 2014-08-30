(ns bmp.rectangle)

(defn replace-middle [start end new-function coll]
  (let [[before not-before] (split-at start coll)
        [middle after] (split-at (- end start) not-before)]
    (concat before (map new-function middle) after)))

(defn fill-rectangle [color x1 y1 x2 y2 bitmap]
  (assoc bitmap :pixels
    (replace-middle y1 y2 (partial replace-middle x1 x2 (constantly color)) (:pixels bitmap))))
