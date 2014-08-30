(ns bmp.file-format)

(def bmp-signature 0x4D42)

(declare read-pixels)

(defn read-file [filename]
  (let [file (java.io.RandomAccessFile. filename "r")
        channel (.getChannel file)
        buffer (.map channel java.nio.channels.FileChannel$MapMode/READ_ONLY, 0, (.size channel))
        _ (.order buffer java.nio.ByteOrder/LITTLE_ENDIAN)
        header (hash-map :signature (.getShort buffer)
                         :filesize (.getInt buffer)
                         :_1 (.getInt buffer)
                         :offset (.getInt buffer)
                         :_2 (.getInt buffer)
                         :width (.getInt buffer)
                         :height (.getInt buffer)
                         :plane-count (.getShort buffer)
                         :colors (.getShort buffer)
                         :compression (.getInt buffer)
                         )]
    (assert (= (:signature header) bmp-signature) "Not a BMP file")
    (assert (= (:plane-count header) 1) "Must have 1 color plane")
    (assert (= (:colors header) 32) "Must have 32 bits per color")
    (assert (= (:compression header) 0) "Must be uncompressed")
    {:width (Math/abs (:width header))
     :height (Math/abs (:height header))
     :pixels (read-pixels header buffer)
     }))

(defn write-file [filename bitmap]
  (let [bitmap-size (* (:width bitmap) (:height bitmap) 4)
        filesize (+ bitmap-size 54)
        file (java.io.RandomAccessFile. filename "rw")
        channel (.getChannel file)
        buffer (.map channel java.nio.channels.FileChannel$MapMode/READ_WRITE, 0, filesize)
        _ (.order buffer java.nio.ByteOrder/LITTLE_ENDIAN)]
    (.putShort buffer bmp-signature)
    (.putInt buffer filesize)
    (.putInt buffer 0); reserved
    (.putInt buffer 54) ; offset
    (.putInt buffer 40) ; header length
    (.putInt buffer (:width bitmap))
    (.putInt buffer (- (:height bitmap))) ; y is always top-to-bottom in our BMPs
    (.putShort buffer 1) ; planes
    (.putShort buffer 32) ; bits per pixel
    (.putInt buffer 0) ; no compression
    (.putInt buffer bitmap-size)
    (.putInt buffer 2835) ; pixels per meter
    (.putInt buffer 2835) ; pixels per meter
    (.putLong buffer 0) ; important colors
    (.put buffer (byte-array (apply concat (apply concat (:pixels bitmap)))))
    (.close file)))

(defn ->unsigned [byte]
  (if (>= byte 0)
    byte
    (bit-and 0xff (short byte))))

(declare flip-pixels)

(defn read-pixels [header buffer]
  (let [abs-height (Math/abs (:height header))
        abs-width (Math/abs (:width header))
        byte-size (* abs-width abs-height 4)
        bytes (byte-array byte-size)
        _ (.position buffer (:offset header))
        _ (.get buffer bytes)
        unflipped-pixels (partition abs-width (partition 4 (map ->unsigned bytes)))]
    (flip-pixels header unflipped-pixels)))

(defn flip-pixels
  "Internal representation of BMPs is ALWAYS top-to-bottom, left-to-right"
  [header unflipped-pixels]
  (let [x-flipped-pixels (if (> (:width header) 0) unflipped-pixels (map reverse unflipped-pixels))
        xy-flipped-pixels (if (< (:height header) 0) x-flipped-pixels (reverse x-flipped-pixels))]
    xy-flipped-pixels))

