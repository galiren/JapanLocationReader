package me.galiren.reader

import me.galiren.reader.Location.Companion.createLocationFromString
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripperByArea
import java.awt.geom.Rectangle2D
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val REGION_NAME = "region"
private const val GET_SQLITE_CONNECTION_EXCEPTION = "Get SQLite Connection Failed."

object LocationInfoParser {

    private fun createSqliteDb(path: String): Connection? {
        val url = "jdbc:sqlite:locations.db"
        try {
            val connection = DriverManager.getConnection(url)
            val sql = "CREATE TABLE IF NOT EXISTS locations (id TEXT PRIMARY KEY, kanji TEXT NOT NULL, " +
                "kana TEXT NOT NULL, classification TEXT NOT NULL)"
            val stmt = connection.createStatement()
            stmt.execute(sql)
            return connection
        } catch (e: SQLException) {
            e.printStackTrace()
            return null
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun parseAndExportToYaml(
        document: PDDocument,
        stripArea: Rectangle2D,
        outputPath: String,
    ) {
        val connection = createSqliteDb(outputPath)
        require(connection != null) {
            println(GET_SQLITE_CONNECTION_EXCEPTION)
        }
        val insertSql = "INSERT INTO locations(id,kanji,kana,classification) VALUES(?, ?, ?, ?)"
        val pstmt = connection.prepareStatement(insertSql)
        val uuidSet = HashSet<String>()
        val stripper = PDFTextStripperByArea()
        stripper.addRegion(REGION_NAME, stripArea)

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
                    var uuid = ""
                    while (true) {
                        uuid = Uuid.random().toString()
                        if (uuid in uuidSet) {
                            continue
                        } else {
                            uuidSet.add(uuid)
                            break
                        }
                    }
                    val location = createLocationFromString(subLine)
                    pstmt.setString(1, uuid)
                    pstmt.setString(2, location.kanji)
                    pstmt.setString(3, location.kana)
                    pstmt.setString(4, location.classification)
                    pstmt.executeUpdate()
                }
                return@forEachIndexed
            }
        }
    }
}
