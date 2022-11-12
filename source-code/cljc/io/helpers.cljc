
;; -- Namespace ---------------------------------------------------------------
;; ----------------------------------------------------------------------------

(ns io.helpers
    (:require [candy.api         :refer [param return]]
              [mid-fruits.map    :as map]
              [regex.api         :refer [re-match? re-mismatch?]]
              [mid-fruits.string :as string]
              [mid-fruits.vector :as vector]))



;; -- Names -------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @name filepath
;  "my-directory/my-file.ext"
;
; @name filename
;  "my-file.ext"
;
; @name extension
;  ".ext"
;
;  Ha egy fájl neve egyetlen "." karaktert tartalmaz és az a fájl nevének első
;  karaktere, akkor a "." karakter után következő rész a fájl nevének tekintendő,
;  nem pedig a fájl kiterjesztésének!
;  Egyes operációs rendszerek a "." karakterrel kezdődő fájlnevű fájlokat rejtett
;  fájloknak tekintik.
;
; @name basename
;  "my-file"
;
; @name basepath
;  "my-directory"



;; -- Configuration -----------------------------------------------------------
;; ----------------------------------------------------------------------------

; @constant (string)
(def FILENAME-PATTERN       #"^[a-zA-Z0-9àèìòùÀÈÌÒÙáéíóúýÁÉÍÓÚÝâêîôûÂÊÎÔÛãñõÃÑÕäëïöüÿÄËÏÖÜŸçÇßØøÅåÆæœ._ \-#()?!+%=]+$")

; @constant (string)
(def DIRECTORY-NAME-PATTERN #"^[a-zA-Z0-9àèìòùÀÈÌÒÙáéíóúýÁÉÍÓÚÝâêîôûÂÊÎÔÛãñõÃÑÕäëïöüÿÄËÏÖÜŸçÇßØøÅåÆæœ._ \-#()?!+%=]+$")

; @constant (integer)
(def MAX-FILENAME-LENGTH 32)

; @constant (map)
(def MIME-TYPES {"aac"  "audio/aac"
                 "avi"  "video/x-msvideo"
                 "bin"  "application/octet-stream"
                 "bmp"  "image/bmp"
                 "bz"   "application/x-bzip"
                 "bz2"  "application/x-bzip2"
                 "css"  "text/css"
                 "doc"  "application/msword"
                 "docx" "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                 "gif"  "image/gif"
                 "htm"  "text/html"
                 "html" "text/html"
                 "ico"  "image/vnd.microsoft.icon"
                 "jar"  "application/java-archive"
                 "jpg"  "image/jpeg"
                 "jpeg" "image/jpeg"
                 "js"   "text/javascript"
                 "mpeg" "video/mpeg"
                 "mp3"  "audio/mpeg"
                 "mp4"  "video/mp4"
                 "m4a"  "audio/m4a"
                 "m4v"  "video/mp4"
                 "odp"  "application/vnd.oasis.opendocument.presentation"
                 "ods"  "application/vnd.oasis.opendocument.spreadsheet"
                 "odt"  "application/vnd.oasis.opendocument.text"
                 "otf"  "font/otf"
                 "png"  "image/png"
                 "pdf"  "application/pdf"
                 "ppt"  "application/vnd.ms-powerpoint"
                 "rar"  "application/x-rar-compressed"
                 "rtf"  "application/rtf"
                 "svg"  "image/svg+xml"
                 "tar"  "application/x-tar"
                 "tif"  "image/tiff"
                 "tiff" "image/tiff"
                 "ttf"  "font/ttf"
                 "txt"  "text/plain"
                 "wav"  "audio/wav"
                 "weba" "audio/webm"
                 "webm" "video/webm"
                 "webp" "image/webp"
                 "xml"  "text/xml"
                 "xls"  "application/vnd.ms-excel"
                 "xlsx" "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                 "zip"  "application/zip"
                 "7z"   "application/x-7z-compressed"})

; @constant (map)
(def EXTENSIONS (map/swap MIME-TYPES))

; @constant (strings in vector)
;  A rendszer által ismert képformátumok. A lista tetszés szerint bővíthető.
(def IMAGE-EXTENSIONS ["bmp" "gif" "jpg" "jpeg" "png" "tif" "tiff" "webp"])



;; -- Filesize ----------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn B->KB  [n] (/ n 1000))
(defn B->MB  [n] (/ n 1000000))
(defn B->GB  [n] (/ n 1000000000))
(defn KB->B  [n] (* n 1000))
(defn KB->MB [n] (/ n 1000))
(defn KB->GB [n] (/ n 1000000))
(defn MB->B  [n] (* n 1000000))
(defn MB->KB [n] (* n 1000))
(defn MB->GB [n] (/ n 1000))



;; -- MIME types --------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn mime-type->extension
  ; @param (string) mime-type
  ;
  ; @example
  ;  (mime-type->extension "text/xml")
  ;  =>
  ;  "xml"
  ;
  ; @example
  ;  (mime-type->extension "foo/bar")
  ;  =>
  ;  "unknown"
  ;
  ; @return (string)
  [mime-type]
  (get EXTENSIONS (string/lowercase mime-type) "unknown"))

(defn extension->mime-type
  ; @param (extension)
  ;
  ; @example
  ;  (extension->mime-type "xml")
  ;  =>
  ;  "text/xml"
  ;
  ; @example
  ;  (extension->mime-type "bar")
  ;  =>
  ;  "unknown/unknown"
  ;
  ; @return (string)
  [extension]
  (get MIME-TYPES (string/lowercase extension) "unknown/unknown"))

(defn unknown-mime-type?
  ; @param (string) mime-type
  ;
  ; @example
  ;  (unknown-mime-type? "text/xml")
  ;  =>
  ;  false
  ;
  ; @example
  ;  (unknown-mime-type? "foo/bar")
  ;  =>
  ;  true
  ;
  ; @return (boolean)
  [mime-type]
  (nil? (mime-type->extension mime-type)))



;; -- File --------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn extension->image?
  ; @param (string) extension
  ;
  ; @example
  ;  (extension->image? "png")
  ;  =>
  ;  true
  ;
  ; @return (boolean)
  [extension]
  (vector/contains-item? IMAGE-EXTENSIONS extension))

(defn mime-type->image?
  ; @param (string) extension
  ;
  ; @example
  ;  (mime-type->image? "image/png")
  ;  =>
  ;  true
  ;
  ; @return (boolean)
  [mime-type]
  (string/starts-with? mime-type "image"))

(defn filepath->directory-path
  ; @param (string) filepath
  ;
  ; @example
  ;  (filepath->directory-path "a/b.png")
  ;  =>
  ;  "a"
  ;
  ; @return (string)
  [filepath]
  (string/before-last-occurence filepath "/" {:return? false}))

(defn filepath->filename
  ; @param (string) filepath
  ;
  ; @example
  ;  (filepath->filename "a/b.png")
  ;  =>
  ;  "b.png"
  ;
  ; @return (string)
  [filepath]
  (string/after-last-occurence filepath "/" {:return? true}))

(defn filepath->extension
  ; @param (string) filepath
  ;
  ; @example
  ;  (filepath->extension "a/b.PNG")
  ;  =>
  ;  "png"
  ;
  ; @example
  ;  (filepath->extension "a/.hidden-file.txt")
  ;  =>
  ;  "txt"
  ;
  ; @example
  ;  (filepath->extension "a/.hidden-file")
  ;  =>
  ;  nil
  ;
  ; @return (string)
  [filepath]
  (let [filename (-> filepath filepath->filename (string/not-starts-with! "."))]
       (if-let [extension (string/after-last-occurence filename "." {:return? false})]
               (string/lowercase extension))))

(defn filename->extension
  ; @param (string) filename
  ;
  ; @return (string)
  [filename]
  (filepath->extension filename))

(defn filename->basename
  ; @param (string) filename
  ;
  ; @example
  ;  (filename->basename "b.png")
  ;  =>
  ;  "b"
  ;
  ; @example
  ;  (filename->basename ".hidden-file.txt")
  ;  =>
  ;  ".hidden-file"
  ;
  ; @example
  ;  (filename->basename ".hidden-file")
  ;  =>
  ;  ".hidden-file"
  ;
  ; @return (string)
  [filename]
  (if-let [extension (filename->extension filename)]
          (string/before-last-occurence filename (str "." extension))
          (return filename)))

(defn filepath->basename
  ; @param (string) filepath
  ;
  ; @example
  ;  (filepath->basename "a/b.png")
  ;  =>
  ;  "b"
  ;
  ; @example
  ;  (filepath->basename "a/.hidden-file.txt")
  ;  =>
  ;  ".hidden-file"
  ;
  ; @example
  ;  (filepath->basename "a/.hidden-file")
  ;  =>
  ;  ".hidden-file"
  ;
  ; @return (string)
  [filepath]
  (-> filepath filepath->filename filename->basename))

(defn filepath->mime-type
  ; @param (string) filepath
  ;
  ; @example
  ;  (filepath->mime-type "a/b.png")
  ;  =>
  ;  "image/png"
  ;
  ; @example
  ;  (filepath->mime-type "a/b")
  ;  =>
  ;  "unknown/unknown"
  ;
  ; @return (string)
  [filepath]
  (-> filepath filepath->extension extension->mime-type))

(defn filename->mime-type
  ; @param (string) filename
  ;
  ; @return (string)
  [filename]
  (filepath->mime-type filename))

(defn filepath->image?
  ; @param (string) filepath
  ;
  ; @usage
  ;  (filepath->image? "a/b.png")
  ;
  ; @return (boolean)
  [filepath]
  (-> filepath filepath->extension extension->image?))

(defn filename->image?
  ; @param (string) filename
  ;
  ; @return (boolean)
  [filename]
  (filepath->image? filename))



;; -- Directory ---------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn directory-path->directory-name
  ; @param (string) directory-path
  ;
  ; @example
  ;  (directory-path->directory-name "a/b")
  ;  =>
  ;  "b"
  ;
  ; @return (string)
  [directory-path]
  (if-let [directory-name (string/after-last-occurence directory-path "/")]
          (return directory-name)
          (return directory-path)))



;; -- Validators ---------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn filename-valid?
  ; @param (string) filename
  ;
  ; @return (boolean)
  [filename]
  (re-match? filename FILENAME-PATTERN))

(defn filename-invalid?
  ; @param (string) filename
  ;
  ; @return (boolean)
  [filename]
  (re-mismatch? filename FILENAME-PATTERN))

(defn directory-name-valid?
  ; @param (string) directory-name
  ;
  ; @return (boolean)
  [directory-name]
  (re-match? directory-name DIRECTORY-NAME-PATTERN))

(defn directory-name-invalid?
  ; @param (string) directory-name
  ;
  ; @return (boolean)
  [directory-name]
  (re-mismatch? directory-name DIRECTORY-NAME-PATTERN))
