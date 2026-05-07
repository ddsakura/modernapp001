package cc.ddsakura.modernapp001

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DataUriParserTest {
    @Test
    fun parse_returnsMimeTypeAndBase64Payload() {
        val result = DataUriParser.parse("data:image/png;base64,iVBORw0KGgo=")

        assertEquals(ParsedDataUri("image/png", "iVBORw0KGgo="), result)
    }

    @Test
    fun parse_acceptsUppercaseBase64Marker() {
        val result = DataUriParser.parse("data:image/jpeg;BASE64,/9j/4AAQSkZJRg==")

        assertEquals(ParsedDataUri("image/jpeg", "/9j/4AAQSkZJRg=="), result)
    }

    @Test
    fun parse_acceptsUppercaseDataScheme() {
        val result = DataUriParser.parse("DATA:image/webp;base64,UklGRiIAAABXRUJQVlA4")

        assertEquals(ParsedDataUri("image/webp", "UklGRiIAAABXRUJQVlA4"), result)
    }

    @Test
    fun parse_acceptsMimeTypeParametersBeforeBase64Marker() {
        val result = DataUriParser.parse("data:image/png;charset=utf-8;base64,iVBORw0KGgo=")

        assertEquals(ParsedDataUri("image/png", "iVBORw0KGgo="), result)
    }

    @Test
    fun parse_preservesPayloadCommasAfterFirstSeparator() {
        val result = DataUriParser.parse("data:image/svg+xml;base64,PHN2ZyA+,more-data")

        assertEquals(ParsedDataUri("image/svg+xml", "PHN2ZyA+,more-data"), result)
    }

    @Test
    fun parse_returnsNullWhenUriIsNotDataUri() {
        assertNull(DataUriParser.parse("https://example.com/image.png"))
    }

    @Test
    fun parse_returnsNullWhenMissingBase64Marker() {
        assertNull(DataUriParser.parse("data:image/png,iVBORw0KGgo="))
    }

    @Test
    fun parse_returnsNullWhenMimeTypeIsBlank() {
        assertNull(DataUriParser.parse("data:;base64,iVBORw0KGgo="))
    }

    @Test
    fun parse_returnsNullWhenMissingCommaSeparator() {
        assertNull(DataUriParser.parse("data:image/png;base64"))
    }
}
