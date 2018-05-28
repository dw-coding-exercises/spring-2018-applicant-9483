(ns my-exercise.search
  (:require [hiccup.page :refer [html5]]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [my-exercise.us-state :as us-state]
            [clojure.pprint :as pp]
            [clj-http.client :as http]))

;; Things I’d do next/had more time:
;; 	• Form validation (right kinds of data)
;; 	• More error checking, tests, handle no active votes at a location
;; 	• Account for multiple elections depending on the data
;; 	• Convert or wrap EDN params the body EDN to a structure we could pull out data from with get-in etc.
;; 	• Setup debugging, haven’t done it with ring starting in this way
;; 	• Might take a look at it in a ClojureScript framework, been wanting to learn about http://fulcro.fulcrologic.com/
;;    I’ve done some API calls in ClojureScript using XhrIo from the Google Closure library so this was a little new.
;;    Did time beyond the 2hrs reading up on Ring, Compojure and how to call the API with clj-http.

(defn header [_]
  [:head
   [:meta {:charset "UTF-8"}]
   [:meta {:name    "viewport"
           :content "width=device-width, initial-scale=1.0, maximum-scale=1.0"}]
   [:title "Search Functionality"]
   [:link {:rel "stylesheet" :href "default.css"}]])

; simple, works but displays edn
#_(defn call-remote [request uri]
  (let [url uri]
  (str (http/get url))
  ))

(defn call-remote [request uri]
  "Using the turbovote.org uri we display the upcoming election results to the user"
  (let [url uri
        edn (http/get url)
        {:keys [body]} edn]
    ; raw body output
    (str body)
    ; todo trying to pull out just the data of interest...
    ; (get-in (clojure.edn/read-string body) [:district-divisions])
    ))

(defn injest [request]
  "Takes form post parameters from http://localhost:3000 and creates an OCD url for use in the turbovote.org API"
  ; sanity checks
  [:h1 "hello world"]
  [:p (str request)]

  [:p (get (:form-params request) "street")]

  (let [street (get (:form-params request) "street")
        street2 (get (:form-params request) "street-2")
        city (get (:form-params request) "city")
        state (get (:form-params request) "state")
        zip (get (:form-params request) "zip")
        ;; one way: ocd (str "ocd-division/country:us/state:" (clojure.string/lower-case state) "/place:" (clojure.string/lower-case city))
        ;; another way:
        ;; ocd (-> "ocd-division/country:us/state:"
        ;;        (str state)
        ;;        (str "/place:")
        ;;        (str city)
        ;;        (clojure.string/lower-case)
        ;;        (clojure.string/replace " " "_"))

        ocd (-> "https://api.turbovote.org/elections/upcoming?district-divisions=ocd-division/country:us/state:"
                (str state)
                (str ",")
                (str "ocd-division/country:us/state:")
                (str state)
                (str "/place:")
                (str city)
                (clojure.string/lower-case)
                (clojure.string/replace " " "_"))]
    ;; quick test
    #_(str street " " street2 " " city " " state " " zip)
    ;; check to see that the ocd url is correct
    ;; (str ocd)
    (call-remote request ocd)))

(defn page [request]
  (html5
    (header request)
    (injest request)))