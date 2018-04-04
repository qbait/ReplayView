package eu.szwiec.replayview.replay

import eu.szwiec.replayview.FileUtils
import eu.szwiec.replayview.SDKConstants
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.FileInputStream
import java.util.*

object FilesProvider {

    fun getFiles(zipPath: String?, types: List<Type>): List<ReplayFile> {
        if(zipPath == null) return emptyList()

        val extractedDir = extractDir(zipPath)

        return getFilesForAllTypes(types, extractedDir)
    }

    private fun extractDir(zipPath: String): File {
        val destinationPath = getDestinationPath(zipPath)
        val isUnzipSuccessful = FileUtils.unzip(zipPath, destinationPath)

        if (!isUnzipSuccessful) throw RuntimeException("Problem with unzipping")

        return File(destinationPath)
    }

    private fun getDestinationPath(zipPath: String): String {
        val filenameWithExtension = zipPath.substring(zipPath.lastIndexOf("/") + 1)
        return SDKConstants.SDCARD_PATH + FilenameUtils.removeExtension(filenameWithExtension)
    }

    private fun getFilesForAllTypes(types: List<Type>, dir: File): List<ReplayFile> {
        val files = ArrayList<ReplayFile>()

        for (type in types) {
            files.addAll(getFilesForType(type, dir))
        }
        return files
    }

    private fun getFilesForType(type: Type, dir: File): List<ReplayFile> {
        val files = ArrayList<ReplayFile>()

        val names = dir.list { d, name ->
            val regex = String.format(".*\\.%s\\d+", type.fileExtension)
            name.matches(regex.toRegex())
        }

        for (name in names) {
            val stream = FileInputStream(dir.toString() + "/" + name)
            files.add(ReplayFile(stream, type))
        }

        return files
    }
}
