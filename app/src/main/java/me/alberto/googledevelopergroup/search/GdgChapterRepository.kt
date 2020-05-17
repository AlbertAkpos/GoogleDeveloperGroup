package me.alberto.googledevelopergroup.search

import android.location.Location
import kotlinx.coroutines.*
import me.alberto.googledevelopergroup.network.GdgApiService
import me.alberto.googledevelopergroup.network.GdgChapter
import me.alberto.googledevelopergroup.network.GdgResponse
import me.alberto.googledevelopergroup.network.LatLong

class GdgChapterRepository(gdgApiService: GdgApiService) {

    private val request = gdgApiService.getChapters()

    private var inProgressSort: Deferred<SortedData> ? = null
    var isFullyInitialized = false
        private set


    suspend fun getChaptersForFilter(filter: String?) : List<GdgChapter> {
        val data = sortedData()
        return when(filter) {
            null -> data.chapters
            else -> data.chaptersByRegion.getOrElse(filter){ emptyList()}
        }
    }

    suspend fun getFilters(): List<String> = sortedData().filters

    private suspend fun sortedData(): SortedData {
        return withContext(Dispatchers.Main){
            inProgressSort?.await() ?: doSortedData()

            doSortedData()
        }
    }

    private suspend fun doSortedData(location: Location? = null): SortedData {
        val result = coroutineScope {
            val deferred = async { SortedData.from(request.await(), location) }

            inProgressSort = deferred
            deferred.await()
        }

        return result
    }


    suspend fun onLocationChanged(location: Location) {
        withContext(Dispatchers.Main) {
            isFullyInitialized = true

            inProgressSort?.cancel()

            doSortedData(location)
        }
    }

    private class SortedData private constructor(
        val chapters: List<GdgChapter>,
        val filters: List<String>,
        val chaptersByRegion: Map<String, List<GdgChapter>>
    ) {
        companion object {
            //sort data by location
            suspend fun from(response: GdgResponse, location: Location?):
                    SortedData {
                return withContext(Dispatchers.Default) {
                    val chapters: List<GdgChapter> = response.chapters.sortedByDistanceFrom(location)

                    val filters : List<String> = chapters.map { it.region }.distinctBy { it }

                    val chaptersByRegion: Map<String, List<GdgChapter>> = chapters.groupBy { it.region }
                    SortedData(chapters, filters, chaptersByRegion)
                }
            }


            private fun List<GdgChapter>.sortedByDistanceFrom(currentLocation: Location?): List<GdgChapter>{
                currentLocation ?: return this

                return sortedBy {
                    distanceBetween(it.geo, currentLocation)
                }
            }

            private fun distanceBetween(start: LatLong, currentLocation: Location) : Float{
                val result = FloatArray(3)
                Location.distanceBetween(start.lat, start.long, currentLocation.latitude, currentLocation.longitude, result)
                return result[0]
            }

        }
    }
}