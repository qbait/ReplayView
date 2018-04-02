package eu.szwiec.replayview.replay

import java.util.Collections

/**
 * Created by szwiec on 07/07/2017.
 */

class ImportDataManager private constructor(private val dataTypes: List<Type>) {

    private fun sortByTimestamp(events: List<ReplayEvent>): List<ReplayEvent> {
        if (dataTypes.size > 1) {
            Collections.sort(events) { o1, o2 -> java.lang.Long.compare(o1.nanoTimestamp, o2.nanoTimestamp) }
        }

        return events
    }

    companion object {

        fun importData(zipPath: String, dataTypes: List<Type>): List<ReplayEvent> {
            val manager = ImportDataManager(dataTypes)

            val extensions = dataTypes.map { it.fileExtension }
            val files = FilesProvider.getFiles(zipPath, extensions)
            val filesArray = files.toTypedArray()
            val events = EventsParser.getEvents(*filesArray)

            return manager.sortByTimestamp(events)
        }
    }

}
