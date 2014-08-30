(ns bmp.mirror)

(defn mirror-x
  "Mirror image horizontally"
  [bitmap]
  (assoc bitmap :pixels (map reverse (:pixels bitmap))))

(defn mirror-y
  "Mirror image vertically"
  [bitmap]
  (assoc bitmap :pixels (reverse (:pixels bitmap))))
