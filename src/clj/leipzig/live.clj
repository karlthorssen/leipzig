(ns leipzig.live
  (:require [overtone.at-at :as at-at]
            [leipzig.melody :as melody]))

(defmulti play-note
  "Plays a note according to its :part.
  e.g. (play-note {:part :bass :time _})"
  :part)

(def time-pool (at-at/mk-pool))

(defn- trickle [[note & others]]
  (when-let [{epoch :time} note]
    (Thread/sleep (max 0 (- epoch (+ 100 (at-at/now)))))
    (cons note (lazy-seq (trickle others)))))

(def channels (atom []))
(defn- register [channel]
  (swap! channels #(conj % channel))
  channel)

(defn stop
  "Kills all running melodies.
  e.g. (->> melody play)

       ; Later
       (stop)"
  []
  (doseq [channel @channels] (future-cancel channel))
  (reset! channels []))

(defn- translate [notes]
  (->> notes
       (melody/after (-> notes first :time -)) ; Allow for notes that lead in.
       (melody/after 0.1) ; Make sure we have time to realise the seq.
       (melody/where :time (partial * 1000))
       (melody/after (at-at/now))))

(defn play
  "Plays notes now.
  e.g. (->> melody play)"
  [notes]
  (->>
    notes
    translate
    trickle
    (remove :rest?)
    (map (fn [{epoch :time :as note}]
           (at-at/at epoch
                     #(-> note (dissoc :time) play-note)
                     time-pool)))
    dorun
    future))

(defn- forever
  "Lazily loop riff forever. riff must start with a positive :time, otherwise there
  will be a glitch as a new copy of riff is sequenced."
  [riff]
  (let [once-through @riff]
    (concat
      once-through
      (lazy-seq (->> riff
                     forever
                     (melody/after (melody/duration once-through)))))))

(defn jam
  "Plays riff repeatedly, freshly dereferencing it each time
  (riff must be a ref). To terminate the looping, set riff
  to nil.
  e.g. (jam (var melody))

       ; Later...
       (def melody nil)"
  [riff]
  (->> riff forever play))
