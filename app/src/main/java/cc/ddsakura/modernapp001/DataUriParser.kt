package cc.ddsakura.modernapp001

internal data class ParsedDataUri(val mimeType: String, val base64Data: String)

internal object DataUriParser {
    fun parse(uri: String): ParsedDataUri? {
        val commaIndex = uri.indexOf(',')
        if (commaIndex == -1) return null

        val metadata = uri.substring(0, commaIndex)
        val data = uri.substring(commaIndex + 1)

        if (!metadata.startsWith("data:", ignoreCase = true)) return null
        if (!metadata.contains(";base64", ignoreCase = true)) return null

        val mimeType = metadata.drop("data:".length).substringBefore(";")
        if (mimeType.isBlank()) return null

        return ParsedDataUri(mimeType, data)
    }
}
