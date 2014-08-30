(ns bmp.resample
  )

(declare horizontal-resample vertical-resample)

(defn resample [new-width new-height bitmap]
  (-> bitmap
      ((partial horizontal-resample new-width))
      ((partial vertical-resample new-height))))


(defn average-pixels [pixels]
  (map #(unchecked-divide-int % (count pixels))
       (reduce #(map + %1 %2)
               pixels)))

(defn stretch-row [factor row]
  (mapcat (partial repeat factor) row))

(defn squeeze-row [factor row]
  (map average-pixels (partition factor row)))

(defn average-rows [rows]
  (map average-pixels (apply mapv vector rows)))

(defn gcd
  "Greatest common divisor of x and y, Euclidean algorithm"
  [a b]
  (loop [a a b b]
    (if (= a 0)
      b
      (if (> b a)
        (recur a (- b a))
        (recur (- a b) b)))))

(defn stretch-squeeze [old-dimension new-dimension]
  (let [dimension-gcd (gcd old-dimension new-dimension)]
    [(/ new-dimension dimension-gcd) (/ old-dimension dimension-gcd)]))

(defmacro dimension-resample [bitmap dimension-key new-value stretch-function squeeze-function]
  `(if (= (~dimension-key ~bitmap) ~new-value)
     ~bitmap
     (let [[stretch-factor# squeeze-factor#] (stretch-squeeze (~dimension-key ~bitmap) ~new-value)
           pixels# (:pixels ~bitmap)
           stretched-pixels# (if (= stretch-factor# 1)
                               pixels#
                               (~stretch-function stretch-factor# pixels#))
           squeezed-pixels# (if (= squeeze-factor# 1)
                              stretched-pixels#
                              (~squeeze-function squeeze-factor# stretched-pixels#)
                              )]
       (assoc ~bitmap ~dimension-key ~new-value :pixels squeezed-pixels#))))

(defn horizontal-resample [new-width bitmap]
  (dimension-resample bitmap
                      :width
                      new-width
                      #(map (partial stretch-row %1) %2)
                      #(map (partial squeeze-row %1) %2)))

(defn vertical-resample [new-height bitmap]
  (dimension-resample bitmap
                      :height
                      new-height
                      #(mapcat (partial repeat %1) %2)
                      #(map average-rows (partition %1 %2))))
