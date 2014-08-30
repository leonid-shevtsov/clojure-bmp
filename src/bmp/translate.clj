(ns bmp.translate)

(defn translate-x [amount bitmap]
  (if (= amount 0)
    bitmap
    (let [shift-amount (- (:width bitmap) (mod amount (:width bitmap)))]
      (assoc bitmap :pixels
                    (map #(apply concat (reverse (split-at shift-amount %))) (:pixels bitmap))))))

(defn translate-y [amount bitmap]
  (if (= amount 0)
    bitmap
    (let [shift-amount (- (:height bitmap) (mod amount (:height bitmap)))]
      (assoc bitmap :pixels
                    (apply concat (reverse (split-at shift-amount (:pixels bitmap))))))))

(defn translate [[amount-x amount-y] bitmap]
  (-> bitmap
      ((partial translate-x amount-x))
      ((partial translate-y amount-y))))
