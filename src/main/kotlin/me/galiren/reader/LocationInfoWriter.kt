package me.galiren.reader

import java.io.File

private const val OUTPUT_FILE_EXISTS_ERROR_MESSAGE = "file exists: "
private const val OUTPUT_FILE_CREATION_ERROR = "file create: "

class LocationInfoWriter {
    var result: String = ""
        private set

    fun writeLocationToYamlString(location: Location) {
        result += "- kanji: ${location.kanji}\n  kana: ${location.kana}\n  classification: ${location.classification}\n"
    }

    fun toYaml(file: File) {
        require(!file.exists()) {
            println("$OUTPUT_FILE_EXISTS_ERROR_MESSAGE${file.absolutePath}")
        }
        try {
            file.createNewFile()
            file.writeText(result.trim())
        } catch (e: Exception) {
            println(OUTPUT_FILE_CREATION_ERROR)
        }
    }
}
