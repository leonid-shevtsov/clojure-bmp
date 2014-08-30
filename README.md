# clojure-bmp

A library and command-line tool for working with BMP files.

Intended for learning rather than production use.

```
  -i, --in filename.bmp         Input file (required)
  -o, --out filename.bmp        Output file (required)
  -s, --scale WxH               Scale image to WxH pixels
  -t, --translate X,Y           Translate image by X pixels horizontally and Y vertically (positive is down)
      --mirror-x                Mirror image horizontally
      --mirror-y                Mirror image vertically
  -r, --rotate A                Rotate image by A degrees; A must be a multiple of 90
  -f, --fill X1,Y1,X2,Y2,R,G,B  Fill rectangle with a color
```
