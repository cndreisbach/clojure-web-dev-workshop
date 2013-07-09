(ns tailrecursion.presioke.flickr
  (:require-macros
   [alandipert.interpol8 :refer [interpolating]]))

(defn flickr-api-map
  "Create a map to send to the Flickr API via Ajax."
  [method params success]
  (clj->js {"type" "GET"
            "url" "http://api.flickr.com/services/rest/"
            "data" (merge params {"method" method, "format" "json"})
            "jsonpCallback" "jsonFlickrApi"
            "dataType" "jsonp"
            "success" (comp success js->clj)}))

(defn image-url
  "Converts a flickr image response map into an image URL."
  [{:strs [farm server id secret]}]
  (interpolating
   "http://farm#{farm}.staticflickr.com/#{server}/#{id}_#{secret}.jpg"))
