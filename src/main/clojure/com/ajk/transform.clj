(ns transform
  (:require [clojure.xml :refer [parse]]
            [clojure.zip :refer [xml-zip node up]]
            [clojure.data.zip.xml :refer [xml-> xml1-> text attr attr=]]
            [clojure.data.json :refer [write-str]]))

(defn- team-name [event alignment]
  (xml1-> event :team :team-metadata (attr= :alignment alignment) :name (attr :first)))

(defn- sports-event [event]
  {
    :date-time (xml1-> event :event-metadata (attr :start-date-time))
    :home-team (team-name event "home")
    :away-team (team-name event "away")
  })

(defn- tournament-round [round]
  {
    :name (xml1-> round :tournament-round-metadata (attr :round-name))
    :events (for [e (xml-> round :sports-event)]
              (sports-event e))
  })

(defn- tournament-stage [stage]
  {
    :name (xml1-> stage :tournament-stage-metadata (attr :stage-name))
    :rounds (for [r (xml-> stage :tournament-round)]
              (tournament-round r))
  })

(defn- xml-to-map [x]
  (let [m (xml1-> x :tournament :tournament-metadata)
        d (xml1-> x :tournament :tournament-division)
        knockout-stages (xml-> d :tournament-stage :tournament-stage)]
      {
        :title (xml1-> m (attr :tournament-name))
        :start-date-time (xml1-> m (attr :start-date-time))
        :end-date-time (xml1-> m (attr :end-date-time))
        :knockouts (for [s (xml-> d :tournament-stage :tournament-stage)]
                     (tournament-stage s))
      }))

(defn- map-to-json [m]
  (write-str m :escape-unicode false))

(defn xml-to-json [xml]
  (let [x (parse xml)
        x (xml-zip x)
        m (xml-to-map x)]
    (map-to-json m)))