
(ns io.edn
    (:require [candy.api  :refer [return]]
              [io.actions :as actions]
              [io.check   :as check]
              [io.config  :as config]
              [io.read    :as read]
              [pretty.api :as pretty]
              [reader.api :as reader]
              [string.api :as string]
              [vector.api :as vector]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn create-edn-file!
  ; @param (string) filepath
  ; @param (map)(opt) options
  ; {:warn? (boolean)(opt)
  ;   Default: true}
  ;
  ; @usage
  ; (create-edn-file! "my-directory/my-file.edn")
  ;
  ; @return (nil)
  ([filepath]
   (create-edn-file! filepath {}))

  ([filepath {:keys [warn?] :or {warn? true}}]
   (when-not (check/file-exists? filepath)
             (if warn? (println (str config/CREATE-FILE-MESSAGE " \"" filepath "\"")))
             (spit filepath "\n{}"))))

(defn write-edn-file!
  ; @param (string) filepath
  ; @param (*) content
  ; @param (map)(opt) options
  ; {:abc? (boolean)(opt)
  ;   Default: false
  ;  :create? (boolean)(opt)
  ;   Default: false
  ;  :warn? (boolean)(opt)
  ;   Default: true}
  ;
  ; @usage
  ; (write-edn-file! "my-directory/my-file.edn" {...})
  ;
  ; @usage
  ; (write-edn-file! "my-directory/my-file.edn" {...} {...})
  ;
  ; @example
  ; (write-edn-file! "my-directory/my-file.edn" {:b "B" :a "A" :d "D" :c "C"})
  ; (read-file       "my-directory/my-file.edn")
  ; =>
  ; "{:b "B" :a "A" :d "D" :c "C"}"
  ;
  ; @example
  ; (write-edn-file! "my-directory/my-file.edn" {:b "B" :a "A" :d "D" :c "C"} {:abc? true})
  ; (read-file       "my-directory/my-file.edn")
  ; =>
  ; "{:a "A" :b "B" :c "C" :d "D"}"
  ;
  ; @return (string)
  ([filepath content]
   (write-edn-file! filepath content {}))

  ([filepath content {:keys [abc?] :as options}]
   (let [output (pretty/mixed->string content {:abc? abc?})]
        (actions/write-file! filepath (str "\n" output) options)
        (return content))))

(defn read-edn-file
  ; @param (string) filepath
  ; @param (map)(opt) options
  ; {:warn? (boolean)(opt)
  ;   Default: true}
  ;
  ; @usage
  ; (read-edn-file "my-directory/my-file.edn")
  ;
  ; @return (*)
  ([filepath]
   (read-edn-file filepath {}))

  ([filepath options]
   ; The content of an EDN file might be a string ("..."), a vector ("[...]"),
   ; a map ("{...}"), etc.
   (let [file-content (read/read-file filepath options)]
        (if (-> file-content string/trim some?)
            (-> file-content reader/string->mixed)))))

(defn swap-edn-file!
  ; @param (string) filepath
  ; @param (function) f
  ; @param (*) params
  ;
  ; @usage
  ; (swap-edn-file! "my-directory/my-file.edn" assoc-in [:items :xyz] "XYZ")
  ;
  ; @usage
  ; (swap-edn-file! "my-directory/my-file.edn" conj "XYZ")
  ;
  ; @return (*)
  [filepath f & params]
  ; Unlike the other file handling functions, the swap-edn-file! function ...
  ; ... does not take the 'options' parameter.
  ; ... always creates the file if it does not exist!
  ; ... always print a warning message when the file does not exist!
  (let [edn    (read-edn-file    filepath)
        params (vector/cons-item params edn)
        output (apply          f params)]
       (write-edn-file! filepath output)
       (return                   output)))
