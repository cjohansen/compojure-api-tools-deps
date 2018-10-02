# Metosin Compojure Api with tools.deps

This repository is the `metosin/compojure-api`
[simple example](https://github.com/metosin/compojure-api/tree/master/examples/simple)
recreated with tools.deps, and it does not work.

Start the server with:

```sh
clojure -A:dev
```

Then observe the failure by issuing an HTTP request for `swagger.json`:

```sh
curl -i http://localhost:8888/swagger.json
```

The resulting failure is:

```sh
2018-10-01 17:57:38.217:WARN:oejs.HttpChannel:qtp1836226750-14: /swagger.json
java.lang.AbstractMethodError: Method linked/map/LinkedMap.isEmpty()Z is abstract
    at linked.map.LinkedMap.isEmpty(map.cljc)
    at com.fasterxml.jackson.databind.ser.std.MapSerializer.serialize(MapSerializer.java:627)
    at com.fasterxml.jackson.databind.ser.std.MapSerializer.serialize(MapSerializer.java:33)
    at com.fasterxml.jackson.databind.ser.std.MapSerializer.serializeFields(MapSerializer.java:718)
    at com.fasterxml.jackson.databind.ser.std.MapSerializer.serialize(MapSerializer.java:639)
    at com.fasterxml.jackson.databind.ser.std.MapSerializer.serialize(MapSerializer.java:33)
    at com.fasterxml.jackson.databind.ser.DefaultSerializerProvider._serialize(DefaultSerializerProvider.java:480)
    at com.fasterxml.jackson.databind.ser.DefaultSerializerProvider.serializeValue(DefaultSerializerProvider.java:319)
    at com.fasterxml.jackson.databind.ObjectMapper._configAndWriteValue(ObjectMapper.java:3905)
    at com.fasterxml.jackson.databind.ObjectMapper.writeValueAsBytes(ObjectMapper.java:3243)
    at jsonista.core$write_value_as_bytes.invokeStatic(core.clj:229)
    at jsonista.core$write_value_as_bytes.invoke(core.clj:221)
    at muuntaja.format.json$encoder$reify__7025.encode_to_bytes(json.clj:43)
    at muuntaja.core$create_coder$encode__7558.invoke(core.clj:340)
    at clojure.core$update.invokeStatic(core.clj:6143)
    at clojure.core$update.invoke(core.clj:6133)
    at muuntaja.core$create$_handle_response__7625.invoke(core.clj:439)
    at muuntaja.core$create$reify__7627.format_response(core.clj:482)
    at muuntaja.middleware$wrap_format_response$fn__7688.invoke(middleware.clj:132)
    at muuntaja.middleware$wrap_format_negotiate$fn__7681.invoke(middleware.clj:96)
    at ring.middleware.keyword_params$wrap_keyword_params$fn__6342.invoke(keyword_params.clj:53)
    at ring.middleware.nested_params$wrap_nested_params$fn__6400.invoke(nested_params.clj:89)
    at ring.middleware.params$wrap_params$fn__6474.invoke(params.clj:67)
    at compojure.api.middleware$wrap_inject_data$fn__8674.invoke(middleware.clj:96)
    at compojure.api.routes.Route.invoke(routes.clj:90)
    at ring.adapter.jetty$proxy_handler$fn__13296.invoke(jetty.clj:26)
    at ring.adapter.jetty.proxy$org.eclipse.jetty.server.handler.AbstractHandler$ff19274a.handle(Unknown Source)
    at org.eclipse.jetty.server.handler.HandlerWrapper.handle(HandlerWrapper.java:97)
    at org.eclipse.jetty.server.Server.handle(Server.java:499)
    at org.eclipse.jetty.server.HttpChannel.handle(HttpChannel.java:311)
    at org.eclipse.jetty.server.HttpConnection.onFillable(HttpConnection.java:258)
    at org.eclipse.jetty.io.AbstractConnection$2.run(AbstractConnection.java:544)
    at org.eclipse.jetty.util.thread.QueuedThreadPool.runJob(QueuedThreadPool.java:635)
    at org.eclipse.jetty.util.thread.QueuedThreadPool$3.run(QueuedThreadPool.java:555)
    at java.lang.Thread.run(Thread.java:748)
```

I tried to use the exact same dependencies in the leiningen-based example:

```clj
(defproject example "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.10.0-alpha8"]
                 [metosin/compojure-api "2.0.0-alpha26"]
                 [ring "1.7.0"]]
  :ring {:handler example.handler/app}
  :uberjar-name "server.jar"
  :profiles {:dev {:plugins [[lein-ring "0.10.0"]]}})
```

And this somehow works. I've compared dependencies as per `lein deps :tree` and
`clojure -Stree`. The leiningen project has `joda-time` 2.7 where this one ends
up with 2.9.9. The leiningen project has `commons-codec` 1.10, where this one
ends up with 1.11. Finally, the leiningen project has `clojure-complete`, which
this one does not have. Manually adding these dependencies does not affect the
outcome, it still produces the error above.

# Update: Workaround

As per [this
comment](https://github.com/metosin/compojure-api/issues/393#issuecomment-426157111),
the problem is
[this bug in frankiesardo/linked](https://github.com/frankiesardo/linked/issues/8).
Until an update is published, this bug can be worked around by excluding this
dependency and including `ikitommi/linked`:

```clj
{:paths ["src"]
 :deps {org.clojure/clojure {:mvn/version "1.10.0-alpha8"}
        metosin/compojure-api {:mvn/version "2.0.0-alpha26"
                               :exclusions [frankiesardo/linked]}
        ring/ring {:mvn/version "1.7.0"}
        ikitommi/linked {:mvn/version "1.3.1-alpha1"}}
 :aliases {:dev {:main-opts ["-m" "compojure-tools-deps.core"]}}}
```
