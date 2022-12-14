
(ns io.mime-type
    (:require [io.config  :as config]
              [string.api :as string]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn mime-type->extension
  ; @param (string) mime-type
  ;
  ; @example
  ; (mime-type->extension "text/xml")
  ; =>
  ; "xml"
  ;
  ; @example
  ; (mime-type->extension "foo/bar")
  ; =>
  ; "unknown"
  ;
  ; @return (string)
  [mime-type]
  (get config/EXTENSIONS (string/to-lowercase mime-type) "unknown"))

(defn extension->mime-type
  ; @param (extension)
  ;
  ; @example
  ; (extension->mime-type "xml")
  ; =>
  ; "text/xml"
  ;
  ; @example
  ; (extension->mime-type "bar")
  ; =>
  ; "unknown/unknown"
  ;
  ; @return (string)
  [extension]
  (get config/MIME-TYPES (string/to-lowercase extension) "unknown/unknown"))

(defn unknown-mime-type?
  ; @param (string) mime-type
  ;
  ; @example
  ; (unknown-mime-type? "text/xml")
  ; =>
  ; false
  ;
  ; @example
  ; (unknown-mime-type? "foo/bar")
  ; =>
  ; true
  ;
  ; @return (boolean)
  [mime-type]
  (-> mime-type mime-type->extension nil?))

(defn mime-type->audio?
  ; @param (string) extension
  ;
  ; @example
  ; (mime-type->image? "audio/mpeg")
  ; =>
  ; true
  ;
  ; @example
  ; (mime-type->image? "application/pdf")
  ; =>
  ; false
  ;
  ; @return (boolean)
  [mime-type]
  (string/starts-with? mime-type "audio"))

(defn mime-type->image?
  ; @param (string) extension
  ;
  ; @example
  ; (mime-type->image? "image/png")
  ; =>
  ; true
  ;
  ; @example
  ; (mime-type->image? "application/pdf")
  ; =>
  ; false
  ;
  ; @return (boolean)
  [mime-type]
  (string/starts-with? mime-type "image"))

(defn mime-type->text?
  ; @param (string) extension
  ;
  ; @example
  ; (mime-type->image? "text/plain")
  ; =>
  ; true
  ;
  ; @example
  ; (mime-type->image? "application/pdf")
  ; =>
  ; false
  ;
  ; @return (boolean)
  [mime-type]
  (string/starts-with? mime-type "text"))

(defn mime-type->video?
  ; @param (string) extension
  ;
  ; @example
  ; (mime-type->image? "video/mpeg")
  ; =>
  ; true
  ;
  ; @example
  ; (mime-type->image? "application/pdf")
  ; =>
  ; false
  ;
  ; @return (boolean)
  [mime-type]
  (string/starts-with? mime-type "video"))
