(ns org.dipert.swingrepl.main
  "Swing Clojure REPL using BeanShell's JConsole"
  (:require clojure.main)
  (:import (javax.swing JFrame))
  (:gen-class))

(def ^{:doc "Formatted Clojure version string"
       :private true}
     clj-version
     (apply str (interpose \. (map *clojure-version* [:major :minor :incremental]))))

(def ^{:doc "Default REPL display options"
       :private true}
     default-opts
     {:width 600
      :height 400
      :title (str "Clojure " clj-version)
      :on-close JFrame/DISPOSE_ON_CLOSE})

(defn- make-jframe
  [{:keys [title width height on-close]}]
  (doto (JFrame. title)
    (.setSize width height)
    (.setDefaultCloseOperation on-close)
    (.setLocationRelativeTo nil)
    (.setVisible true)))

(defn- make-repl-jframe
  "Displays a JFrame with JConsole and attached REPL."
  ([] (make-repl-jframe {}))
  ([optmap]
     (let [jframe (make-jframe (merge default-opts optmap))]
       (javax.swing.SwingUtilities/invokeLater
	#(let [console (bsh.util.JConsole.)]
	   (doto (.getContentPane jframe)
	     (.setLayout (java.awt.BorderLayout.))
	     (.add console))
	   (binding [*out* (java.io.OutputStreamWriter. (.getOut console))
		     *in*  (clojure.lang.LineNumberingPushbackReader. (.getIn console))]
	     (.start (Thread. (bound-fn [] (clojure.main/repl))))))))))

(defn -main
  [& args]
  (make-repl-jframe {:on-close JFrame/EXIT_ON_CLOSE}))