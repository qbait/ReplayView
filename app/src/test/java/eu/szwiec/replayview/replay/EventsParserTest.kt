package eu.szwiec.replayview.replay

import org.junit.Assert
import org.junit.Test
import java.io.File

class EventsParserTest {

    @Test
    fun getEvents() {
        val files = listOf(getFile("/replay/sample.edyuid0"), getFile("/replay/sample.wifi0"))
        val expectedSize = 368

        val events = EventsParser.getEvents(files)

        Assert.assertEquals(expectedSize, events.size)
        Assert.assertTrue(events.stream().allMatch { e -> e is ReplayEvent })
        Assert.assertEquals(expectedSize, events.size)
    }

    private fun getFile(path: String): File? {
        val url = EventsParserTest::class.java.getResource(path)
        return File(url.toURI())
    }
}