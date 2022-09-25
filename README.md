
# Advanced coroutines with Kotlin Flow and LiveData

A simple android app, that shows how to use the LiveData builder to combine Kotlin coroutines with LiveData. It also uses Coroutines Asynchronous Flow, which is a type from the coroutines library for representing an async sequence (or stream) of values, to implement the same thing.


## Architecture overview
This app uses Architecture Components to separate the UI code in MainActivity and PlantListFragment from the application logic in PlantListViewModel. PlantRepository provides a bridge between the ViewModel and PlantDao, which accesses the Room database to return a list of Plant objects. The UI then takes this list of plants and displays them in RecyclerView grid layout.
