(ns bmp.color)

; Functions that process colors

(defn adjust-pixel-color
  "Multiplies the pixel by a vector of fractional positive coefficients"
  [ks pixel]
  (map #(min 255 (int (* %1 %2))) ks pixel))

(defn negative-pixel
  "Converts pixel to its negative"
  [[b g r a]]
  [(- 255 b) (- 255 g) (- 255 b) a])

(defn monochrome-pixel
  "Converts pixel to an adaptive grayscale color"
  [[b g r a]]
  (let [gray (int (+ (* 0.3 r) (* 0.59 g) (* 0.11 b)))]
    [gray gray gray a]))

(defn per-pixel-filter
  "Applies pixel-function to every pixel of the bitmap"
  [pixel-function bitmap]
  (assoc bitmap :pixels
                (map (fn [row] (map pixel-function row)) (:pixels bitmap))))

(defn adjust-color-balance
  "Multiplies bitmap by given coefficients"
  [[k-red k-green k-blue] bitmap]
  (per-pixel-filter #(adjust-pixel-color [k-blue k-green k-red 1] %) bitmap))

(defn adjust-brightness
  "Adjusts brightness of a bitmap"
  [k-brightness bitmap]
  (adjust-color-balance [k-brightness k-brightness k-brightness] bitmap))

(def negative (partial per-pixel-filter negative-pixel))

(def monochrome (partial per-pixel-filter monochrome-pixel))
