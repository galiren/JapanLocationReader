package me.galiren.reader

class Location private constructor(
    val kanji: String,
    val kana: String,
    val classification: String,
) {
    override fun toString() = "Location(kanji = $kanji, kana = $kana, classification = $classification)"

    companion object {
        fun createLocationFromString(string: String): Location {
            var classification = ""
            val elements = string.split(" ")
            val length = elements.size

            // find classification part
            var shouldTerminate = false
            for (i in length - 1 downTo 0) {
                for (ch in elements[i]) {
                    if (!ch.isLetter()) {
                        shouldTerminate = true
                        break
                    }
                }
                if (shouldTerminate) {
                    shouldTerminate = false
                    break
                }
                classification = elements[i] + " " + classification
            }

            // find kanji and kana part
            var kanjiIndex = 1
            val pureNumberRegex = "^[0-9]*$".toRegex()

            for (i in 1 until length) {
                if (pureNumberRegex.matches(elements[i])) {
                    kanjiIndex++
                } else {
                    break
                }
            }
            return Location(
                kanji = elements[kanjiIndex],
                kana = elements[kanjiIndex + 1],
                classification = classification.trim(),
            )
        }
    }
}
