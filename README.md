
# Advanced coroutines with Kotlin Flow and LiveData

A simple android app, that shows how to use the LiveData builder to combine Kotlin coroutines with LiveData. It also uses Coroutines Asynchronous Flow, which is a type from the coroutines library for representing an async sequence (or stream) of values, to implement the same thing.


## Screenshots

![App Screenshot](https://user-images.githubusercontent.com/60019872/192133627-1ab1da45-9f01-41fe-8e14-beb42c75b72a.png)


## Architecture overview
This app uses `Architecture Components` to separate the UI code in `MainActivity` and `PlantListFragment` from the application logic in `PlantListViewModel`. `PlantRepository` provides a bridge between the `ViewModel` and `PlantDao`, which accesses the `Room` database to return a list of `Plant` objects. The UI then takes this list of plants and displays them in RecyclerView grid layout.
