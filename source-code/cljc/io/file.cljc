
(ns io.file
    (:require [candy.api    :refer [return]]
              [io.mime-type :as mime-type]
              [string.api   :as string]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn extension->audio?
  ; @param (string) extension
  ;
  ; @usage
  ; (extension->audio? "mp3")
  ;
  ; @example
  ; (extension->audio? "mp3")
  ; =>
  ; true
  ;
  ; @return (boolean)
  [extension]
  (-> extension mime-type/extension->mime-type mime-type/mime-type->audio?))

(defn extension->image?
  ; @param (string) extension
  ;
  ; @usage
  ; (extension->image? "png")
  ;
  ; @example
  ; (extension->image? "png")
  ; =>
  ; true
  ;
  ; @return (boolean)
  [extension]
  (-> extension mime-type/extension->mime-type mime-type/mime-type->image?))

(defn extension->text?
  ; @param (string) extension
  ;
  ; @usage
  ; (extension->text? "txt")
  ;
  ; @example
  ; (extension->text? "txt")
  ; =>
  ; true
  ;
  ; @return (boolean)
  [extension]
  (-> extension mime-type/extension->mime-type mime-type/mime-type->text?))

(defn extension->video?
  ; @param (string) extension
  ;
  ; @usage
  ; (extension->video? "mp4")
  ;
  ; @example
  ; (extension->video? "mp4")
  ; =>
  ; true
  ;
  ; @return (boolean)
  [extension]
  (-> extension mime-type/extension->mime-type mime-type/mime-type->video?))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn item-path->parent-path
  ; @param (string) item-path
  ;
  ; @usage
  ; (item-path->parent-path "my-directory/my-subdirectory/my-file.ext")
  ;
  ; @example
  ; (item-path->parent-path "my-directory/my-subdirectory/my-file.ext")
  ; =>
  ; "my-directory/my-subdirectory"
  ;
  ; @example
  ; (item-path->parent-path "my-file.ext")
  ; =>
  ; nil
  ;
  ; @return (string)
  [item-path]
  (string/before-last-occurence item-path "/" {:return? false}))

(defn filepath->directory-path
  ; @param (string) filepath
  ;
  ; @usage
  ; (filepath->directory-path "my-directory/my-subdirectory/my-file.ext")
  ;
  ; @example
  ; (filepath->directory-path "my-directory/my-subdirectory/my-file.ext")
  ; =>
  ; "my-directory/my-subdirectory"
  ;
  ; @example
  ; (filepath->directory-path "my-file.ext")
  ; =>
  ; nil
  ;
  ; @return (string)
  [filepath]
  (item-path->parent-path filepath))

(defn filepath->filename
  ; @param (string) filepath
  ;
  ; @usage
  ; (filepath->filename "my-directory/my-file.ext")
  ;
  ; @example
  ; (filepath->filename "my-directory/my-file.ext")
  ; =>
  ; "my-file.ext"
  ;
  ; @return (string)
  [filepath]
  (string/after-last-occurence filepath "/" {:return? true}))

(defn filepath->extension
  ; @param (string) filepath
  ;
  ; @usage
  ; (filepath->extension "my-directory/my-file.EXT")
  ;
  ; @example
  ; (filepath->extension "my-directory/my-file.EXT")
  ; =>
  ; "ext"
  ;
  ; @example
  ; (filepath->extension "my-directory/.my-hidden-file.ext")
  ; =>
  ; "ext"
  ;
  ; @example
  ; (filepath->extension "my-directory/.my-hidden-file")
  ; =>
  ; nil
  ;
  ; @return (string)
  [filepath]
  (let [filename (-> filepath filepath->filename (string/not-starts-with! "."))]
       (if-let [extension (string/after-last-occurence filename "." {:return? false})]
               (string/to-lowercase extension))))

(defn filename->extension
  ; @param (string) filename
  ;
  ; @usage
  ; (filename->extension "my-file.EXT")
  ;
  ; @example
  ; (filename->extension "my-file.EXT")
  ; =>
  ; "ext"
  ;
  ; @example
  ; (filename->extension ".my-hidden-file.ext")
  ; =>
  ; "ext"
  ;
  ; @example
  ; (filename->extension ".my-hidden-file")
  ; =>
  ; nil
  ;
  ; @return (string)
  [filename]
  (filepath->extension filename))

(defn filename->basename
  ; @param (string) filename
  ;
  ; @usage
  ; (filename->basename "my-file.EXT")
  ;
  ; @example
  ; (filename->basename "my-file.EXT")
  ; =>
  ; "my-file"
  ;
  ; @example
  ; (filename->basename ".my-hidden-file.ext")
  ; =>
  ; ".my-hidden-file"
  ;
  ; @example
  ; (filename->basename ".my-hidden-file")
  ; =>
  ; ".my-hidden-file"
  ;
  ; @return (string)
  [filename]
  (if-let [extension (filename->extension filename)]
          (string/before-last-occurence filename (str "." extension))
          (return filename)))

(defn filepath->basename
  ; @param (string) filepath
  ;
  ; @usage
  ; (filepath->basename "my-directory/my-file.EXT")
  ;
  ; @example
  ; (filepath->basename "my-directory/my-file.EXT")
  ; =>
  ; "my-file"
  ;
  ; @example
  ; (filepath->basename "my-directory/.my-hidden-file.ext")
  ; =>
  ; ".my-hidden-file"
  ;
  ; @example
  ; (filepath->basename "my-directory/.my-hidden-file")
  ; =>
  ; ".my-hidden-file"
  ;
  ; @return (string)
  [filepath]
  (-> filepath filepath->filename filename->basename))

(defn filepath->mime-type
  ; @param (string) filepath
  ;
  ; @usage
  ; (filepath->mime-type "my-directory/my-image.png")
  ;
  ; @example
  ; (filepath->mime-type "my-directory/my-image.png")
  ; =>
  ; "image/png"
  ;
  ; @example
  ; (filepath->mime-type "my-directory/my-file")
  ; =>
  ; "unknown/unknown"
  ;
  ; @return (string)
  [filepath]
  (-> filepath filepath->extension mime-type/extension->mime-type))

(defn filename->mime-type
  ; @param (string) filename
  ;
  ; @usage
  ; (filename->mime-type "my-image.png")
  ;
  ; @example
  ; (filename->mime-type "my-image.png")
  ; =>
  ; "image/png"
  ;
  ; @example
  ; (filename->mime-type "my-file")
  ; =>
  ; "unknown/unknown"
  ;
  ; @return (string)
  [filename]
  (filepath->mime-type filename))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn filepath->audio?
  ; @param (string) filepath
  ;
  ; @usage
  ; (filepath->audio? "my-directory/my-audio.mp3")
  ;
  ; @example
  ; (filepath->audio? "my-directory/my-audio.mp3")
  ; =>
  ; true
  ;
  ; @example
  ; (filepath->audio? "my-directory/my-file.ext")
  ; =>
  ; false
  ;
  ; @example
  ; (filepath->audio? "my-directory/my-file")
  ; =>
  ; false
  ;
  ; @return (boolean)
  [filepath]
  (-> filepath filepath->extension extension->audio?))

(defn filepath->image?
  ; @param (string) filepath
  ;
  ; @usage
  ; (filepath->image? "my-directory/my-image.png")
  ;
  ; @example
  ; (filepath->image? "my-directory/my-image.png")
  ; =>
  ; true
  ;
  ; @example
  ; (filepath->image? "my-directory/my-file.ext")
  ; =>
  ; false
  ;
  ; @example
  ; (filepath->image? "my-directory/my-file")
  ; =>
  ; false
  ;
  ; @return (boolean)
  [filepath]
  (-> filepath filepath->extension extension->image?))

(defn filepath->text?
  ; @param (string) filepath
  ;
  ; @usage
  ; (filepath->text? "my-directory/my-text.txt")
  ;
  ; @example
  ; (filepath->text? "my-directory/my-text.txt")
  ; =>
  ; true
  ;
  ; @example
  ; (filepath->text? "my-directory/my-file.ext")
  ; =>
  ; false
  ;
  ; @example
  ; (filepath->text? "my-directory/my-file")
  ; =>
  ; false
  ;
  ; @return (boolean)
  [filepath]
  (-> filepath filepath->extension extension->text?))

(defn filepath->video?
  ; @param (string) filepath
  ;
  ; @usage
  ; (filepath->video? "my-directory/my-video.mp4")
  ;
  ; @example
  ; (filepath->video? "my-directory/my-video.mp4")
  ; =>
  ; true
  ;
  ; @example
  ; (filepath->video? "my-directory/my-file.ext")
  ; =>
  ; false
  ;
  ; @example
  ; (filepath->video? "my-directory/my-file")
  ; =>
  ; false
  ;
  ; @return (boolean)
  [filepath]
  (-> filepath filepath->extension extension->image?))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn filename->audio?
  ; @param (string) filename
  ;
  ; @usage
  ; (filename->audio? "my-audio.mp3")
  ;
  ; @example
  ; (filename->audio? "my-audio.mp3")
  ; =>
  ; true
  ;
  ; @example
  ; (filename->audio? "my-file.ext")
  ; =>
  ; false
  ;
  ; @example
  ; (filename->audio? "my-file")
  ; =>
  ; false
  ;
  ; @return (boolean)
  [filename]
  (filepath->audio? filename))

(defn filename->image?
  ; @param (string) filename
  ;
  ; @usage
  ; (filename->image? "my-image.png")
  ;
  ; @example
  ; (filename->image? "my-image.png")
  ; =>
  ; true
  ;
  ; @example
  ; (filename->image? "my-file.ext")
  ; =>
  ; false
  ;
  ; @example
  ; (filename->image? "my-file")
  ; =>
  ; false
  ;
  ; @return (boolean)
  [filename]
  (filepath->image? filename))

(defn filename->text?
  ; @param (string) filename
  ;
  ; @usage
  ; (filename->text? "my-text.txt")
  ;
  ; @example
  ; (filename->text? "my-text.txt")
  ; =>
  ; true
  ;
  ; @example
  ; (filename->text? "my-file.ext")
  ; =>
  ; false
  ;
  ; @example
  ; (filename->text? "my-file")
  ; =>
  ; false
  ;
  ; @return (boolean)
  [filename]
  (filepath->text? filename))

(defn filename->video?
  ; @param (string) filename
  ;
  ; @usage
  ; (filename->video? "my-video.mp4")
  ;
  ; @example
  ; (filename->video? "my-video.mp4")
  ; =>
  ; true
  ;
  ; @example
  ; (filename->video? "my-file.ext")
  ; =>
  ; false
  ;
  ; @example
  ; (filename->video? "my-file")
  ; =>
  ; false
  ;
  ; @return (boolean)
  [filename]
  (filepath->image? filename))
