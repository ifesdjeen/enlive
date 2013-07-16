(ns net.cgrand.enlive-html.performance-test
  (:use net.cgrand.enlive-html)
  (:require [net.cgrand.xml :as xml])
  (:require [clojure.zip :as z])
  (:use [clojure.test :only [deftest is are]]))

(defsnippet template-snippet-nested-2 "resources/templates/performance_test.html"
  [:#snippet_template]
  [index]
  [:div] (do-> (add-class "added-class")
               (set-attr (str "attr" index)
                         (str "value" index))
               (append "  some more content")
               (prepend " some prepended content ")
               (append (template-snippet-nested-2 index))
               (fn [a]
                 (assoc a :content (conj (:content a) "___")))))

(defsnippet template-snippet-nested "resources/templates/performance_test.html"
  [:#snippet_template]
  [index]
  [:div] (do-> (add-class "added-class")
               (set-attr (str "attr" index)
                         (str "value" index))
               (append "  some more content")
               (prepend " some prepended content ")
               (fn [a]
                 (assoc a :content (conj (:content a) "___")))))

(defsnippet template-snippet "resources/templates/performance_test.html"
  [:#snippet_template]
  [index]
  [:div] (do-> (add-class "added-class")
               (set-attr (str "attr" index)
                         (str "value" index))
               (append "  some more content")
               (prepend " some prepended content ")
               (append (template-snippet-nested index))
               (fn [a]
                 (assoc a :content (conj (:content a) "___")))))


(deftest performance-test
  (let [html-source (html-snippet (slurp (clojure.java.io/resource "resources/templates/performance_test.html")))]
    (dotimes [i 1000]
      (let [transformed (flatmap
                         (transformation
                          [:.placeholder_level_1] (clone-for [i (range 0 30)]
                                                             [:.placeholder_level_2_1] (content (template-snippet i))
                                                             [:.placeholder_level_2_2] (content (template-snippet i))))
                         html-source)]
        (time
         (apply str (emit* transformed)))))))
