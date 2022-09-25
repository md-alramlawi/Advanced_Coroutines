
# Advanced coroutines with Kotlin Flow and LiveData

A simple android app, that shows how to use the `LiveData` builder to combine Kotlin coroutines with `LiveData`. It also uses Coroutines Asynchronous `Flow`, which is a type from the coroutines library for representing an async sequence (or stream) of values, to implement the same thing.


## Screenshots

![App Screenshot](https://user-images.githubusercontent.com/60019872/192133627-1ab1da45-9f01-41fe-8e14-beb42c75b72a.png)


## Architecture overview
This app uses `Architecture Components` to separate the UI code in `MainActivity` and `PlantListFragment` from the application logic in `PlantListViewModel`. `PlantRepository` provides a bridge between the `ViewModel` and `PlantDao`, which accesses the `Room` database to return a list of `Plant` objects. The UI then takes this list of plants and displays them in `RecyclerView` grid layout.

## Modules
###	1. Plant module:
Inside Plant module we put every common components that shared by multiple modules such as:
-	`Plant class`.
-	`Customized CardView`.
-	`PlantAdapter`.
-	`BindingAdapters`.
-	`util package`.
-   `Mutex`, just like a traffic cop allows the coroutines to access the shared resources in a controlled and safe manner.

###	2. LiveDataApp:
We built the app using `Android Architecture Components`, which uses LiveData to get a list of objects from a `Room` database and display them in a `RecyclerView` grid layout, we also introdused some advanced operators such as:
-	`LiveData builder` to combine `Kotlin coroutines` with `LiveData`.
-   `fun emitSource(source: LiveData<T>)` emits multiple values from a `LiveData` whenever you want to emit a new value. Note that each call to emitSource() removes the previously-added source.
-   `switchMap` will let you point to a new `LiveData` every time a new value is received.
###	2. Flowapp:
We built the same logic using `Flow` from `kotlinx-coroutines` as it is a type that can emit multiple values sequentially, and we introduced some useful built-in operators such as:
-	The `asLiveData` operator that converts a `Flow` into a `LiveData` with a configurable timeout as we want to keep `LiveData` in the `UI layer`.
-	`Flow<T>.conflate()` to run the collector in a separate coroutine, and in that case, the emitter is never suspended due to a slow collector, and the collector always gets the most recent value emitted.
-	`Flow<T>.combine()` operator in order to combine two flows together. In this case, both flows will run in their own coroutine, then whenever either flow produces a new value the transformation will be called with the latest value from either flow.
-	`Flow<T>.flatMapLatest` extensions that allow you to switch between multiple flows.
-	`Flow<T>. map` operator that makes all async operations sequential.

