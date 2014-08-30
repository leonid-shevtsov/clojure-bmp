(ns bmp.translate)

(defn translate-x [amount bitmap]
  (if (= amount 0)
    bitmap
    (assoc bitmap :pixels
                  (map #(apply concat (reverse (split-at (- (:width bitmap) amount) %))) (:pixels bitmap)))))

(defn translate-y [amount bitmap]
  (if (= amount 0)
    bitmap
    (assoc bitmap :pixels
                  (apply concat (reverse (split-at (- (:height bitmap) amount) (:pixels bitmap)))))))

(defn translate [amount-x amount-y bitmap]
  (-> bitmap
      ((partial translate-x amount-x))
      ((partial translate-y amount-y))))
