package me.galiren.reader

import org.apache.pdfbox.Loader
import java.awt.geom.Rectangle2D
import java.io.File
import java.nio.file.InvalidPathException
import kotlin.io.path.Path

private const val HELP_MESSAGE: String = """
    Usage: <jar name> <arg1> -o <arg2>

    Available arg1 options:

    help: print this help info. --help or -h is also acceptable. In this case, the remaining arguments will be omitted
    file path: the file path of GAZETTEER OF JAPAN <https://www.gsi.go.jp/common/000238259.pdf>

    arg2: the desired output database file path.
"""

private const val PDF_READ_ERROR_MESSAGE: String = """
    Error occurs. Aborted.
"""

private const val INVALID_PATH_ERROR_MESSAGE: String = "Invalid path: "

private const val ARG_ERROR_MESSAGE: String = "Arguments have errors. \n $HELP_MESSAGE"

fun main(args: Array<String>) {
    require(args.size == 3) {
        println(ARG_ERROR_MESSAGE)
        return
    }
    require(args[1] == "-o") {
        println(ARG_ERROR_MESSAGE)
        return
    }
    // ensure path is valid
    try {
        val path = Path(args[2])
    } catch (e: InvalidPathException) {
        println("$INVALID_PATH_ERROR_MESSAGE$args[2]")
    }

    if (args[0] == "--help" || args[0] == "help" || args[0] == "-h") {
        println(HELP_MESSAGE)
        return
    }
    val pdfFile = File(args[0])
    try {
        Loader.loadPDF(pdfFile).use { document ->
            val firstLocationListPage = document.getPage(7).mediaBox
            val rect: Rectangle2D = Rectangle2D.Float(0F, 0F, firstLocationListPage.width, firstLocationListPage.height)
            LocationInfoParser.parseAndExportToYaml(document, rect, args[2])
        }
    } catch (_: Exception) {
        println(PDF_READ_ERROR_MESSAGE)
    }
}
