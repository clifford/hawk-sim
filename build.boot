(set-env!
  :source-paths #{"src"}
  :resource-paths #{"resources"}
  :dependencies '[[org.clojure/clojure "1.7.0"]

                  ;; Simulant & Friends
                  [com.datomic/simulant "0.1.7" :exclusions [com.datomic/datomic-free]]
                  [io.homegrown/boot-sim "0.2.2"]
                  [org.craigandera/causatum "0.3.0"]

                  ;; Datomic & Friends
                  [com.datomic/datomic-free "0.9.5130" :exclusions [joda-time
                                                                    org.slf4j/slf4j-nop
                                                                    org.slf4j/slf4j-log4j12]]
                  [io.rkn/conformity "0.3.2" :exclusions [com.datomic/datomic-free]]

                  ;; HTTP Client
                  [clj-http "1.0.1"]

                  ;; JSON Decoding
                  [cheshire "5.3.1"]

                  ;; Environment Variables
                  [environ "0.5.0"]

                  ;; Logging
                  [adzerk/boot-logservice "1.0.0"]
                  [org.clojure/tools.logging "0.3.1"]
                  [org.slf4j/slf4j-api "1.7.6"]
                  [ch.qos.logback/logback-classic "1.1.1"]])


;; Load Datomic data readers
(#'clojure.core/load-data-readers)
(set! *data-readers* (.getRawRoot #'*data-readers*))

(def +version+ "0.1.0")

(task-options! pom {:project 'hawk-sim
                    :version +version+
                    :description "FIXME: write description"
                    :license {"License Name" "All Rights Reserved"}})


;; Setup boot-sim for interacting with sims from the command-line
(require '[io.homegrown.boot-sim :refer :all])

(def datomic-uri "datomic:free://localhost:4334/sample-sim")

(task-options!
  bootstrap       {:uri datomic-uri
                   :bootstrap-fn 'io.datom.hawk-sim.db/bootstrap!}
  create-model    {:uri datomic-uri
                   :create-fn 'io.datom.hawk-sim.model/create-model!
                   :type :model.type/sample}
  list-models     {:uri datomic-uri
                   :list-fn 'io.datom.hawk-sim.model/list-models}
  create-test     {:uri datomic-uri
                   :create-fn 'io.datom.hawk-sim.test/create-test!
                   :host-name "http://192.168.99.100:8080"
                   :duration 1
                   :concurrency 1}
  list-tests      {:uri datomic-uri
                   :list-fn 'io.datom.hawk-sim.test/list-tests}
  run-sim         {:uri datomic-uri
                   :run-fn 'io.datom.hawk-sim.sim/run-sim!
                   :processes 1
                   :speed 1}
  list-sims       {:uri datomic-uri
                   :list-fn 'io.datom.hawk-sim.sim/list-sims}
  validate-sim    {:uri datomic-uri
                   :validate-fn 'io.datom.hawk-sim.validations/validate}
  validate-latest {:uri datomic-uri
                   :validate-fn 'io.datom.hawk-sim.validations/validate
                   :lookup-fn 'io.datom.hawk-sim.sim/latest-sim})
