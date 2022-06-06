# Teapot

An experimental [Tidal](https://tidalcycles.org/) port for Kotlin

## Prerequisites
Install `kotlin` with Sdkman:
```sdk install kotlin```

## Run:

Start SuperDirt

Build and Run the REPL:
```./gradlew clean shadowJar && kotlinc-jvm -cp build/libs/teapot.jar```

From the REPL, load and start Teapot:
```
>>> import org.tidalcycles.teapot.*
>>> start()
```

Now execute a pattern:
```
>>> p[0] = s("bd")
```
and another
```
>>> p["id"] = fastcat(s("bd"), s("sd") fast 2) fast fastcat(pure(2), pure(1), pure(4))
```
some mininotation is supported 
```
>>> p["id"] = s("<[bd sd]*2 hh*4>") fast "2 1 4"
```
