package eu.szwiec.replayview.replay

import eu.szwiec.replayview.FileUtils
import eu.szwiec.replayview.SDKConstants
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.util.*

object FilesProvider {

    fun getFiles(zipPath: String, extensions: List<String>): List<File> {
        val extractedDir = extractDir(zipPath)

        return getMatchingFiles(extensions, extractedDir)
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

    private fun getMatchingFiles(types: List<String>, dir: File): List<File> {
        val files = ArrayList<File>()

        for (type in types) {
            files.addAll(getMatchingFiles(type, dir))
        }
        return files
    }

    private fun getMatchingFiles(type: String, dir: File): List<File> {
        val files = ArrayList<File>()

        val names = dir.list { d, name ->
            val regex = String.format(".*\\.%s\\d+", type)
            name.matches(regex.toRegex())
        }

        for (name in names) {
            val file = File(dir.toString() + "/" + name)
            files.add(file)
        }

        return files
    }
}
