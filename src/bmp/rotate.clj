(ns bmp.rotate)

(defn rotate-90 [bitmap]
  (assoc bitmap :width (:height bitmap)
                :height (:width bitmap)
                :pixels (apply mapv list (reverse (:pixels bitmap)))))

(defn rotate-180 [bitmap]
  (assoc bitmap :pixels (reverse (map reverse (:pixels bitmap)))))

(defn rotate-270 [bitmap]
  (assoc bitmap :width (:height bitmap)
                :height (:width bitmap)
                :pixels (reverse (apply mapv list (:pixels bitmap)))))

(def rotator-fn-map {0 identity 90 rotate-90 180 rotate-180 270 rotate-270})

(defn rotate
  "Rotate bitmap by a multiple of 90 degrees, clockwise or counterclockwise"
  [angle bitmap]
  (let [rotator-fn (rotator-fn-map (mod angle 360))]
    (assert (some? rotator-fn) "Angle must be multiple of 90")
    (rotator-fn bitmap)))
