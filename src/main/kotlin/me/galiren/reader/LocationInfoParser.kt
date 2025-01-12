package me.galiren.reader

import me.galiren.reader.Location.Companion.createLocationFromString
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripperByArea
import java.awt.geom.Rectangle2D
import java.io.File

private const val REGION_NAME = "region"

object LocationInfoParser {
    fun parseAndExportToYaml(
        document: PDDocument,
        stripArea: Rectangle2D,
        outputPath: String,
    ) {
        var number: Int = 1
        val stripper = PDFTextStripperByArea()
        stripper.addRegion(REGION_NAME, stripArea)
        val writer = LocationInfoWriter()
        document.documentCatalog.pages.forEachIndexed { number, page ->
            // page 8 to page 111 is location information list

            if (number in 7..110) {
                stripper.extractRegions(page)
                val contentString = stripper.getTextForRegion(REGION_NAME).split("\n")

                for (line in contentString) {
                    val first = line.indexOfFirst { it.isDigit() }
                    val last = line.indexOfLast { it.isLetter() }
                    if (first == -1 || last == -1) continue
                    val subLine = line.substring(first, last + 1)
                    writer.writeLocationToYamlString(createLocationFromString(subLine))
                }
                return@forEachIndexed
            }
        }
        writer.toYaml(File(outputPath))
    }
}
