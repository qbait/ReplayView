package eu.szwiec.replayview.replay

import eu.szwiec.replayview.otto.EddystoneUidPacketEvent
import eu.szwiec.replayview.otto.SDKWifiScanResultEvent
import junit.framework.Assert.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.io.File

class EventsParserTest {

    @Test
    fun getEvents() {
        val btStream = EventsParserTest::class.java.getResourceAsStream("/replay/sample.edyuid0")
        val wifiStream = EventsParserTest::class.java.getResourceAsStream("/replay/sample.wifi0")
        val btFile = ReplayFile(btStream, Type.BLUETOOTH)
        val wifiFile = ReplayFile(wifiStream, Type.WIFI)

        val expectedSize = 368

        val events = EventsParser.getEvents(listOf(btFile, wifiFile))

        assertEquals(expectedSize, events.size)
        assertTrue(events.stream().allMatch { e -> e is ReplayEvent })

        events.sortBy { it.nanoTimestamp }

        assertTrue(events[2] is EddystoneUidPacketEvent)
        val randomBtEvent = events[2] as EddystoneUidPacketEvent
        assertTrue(randomBtEvent is EddystoneUidPacketEvent)
        assertEquals(randomBtEvent.deviceAddress, "CB:3B:9B:41:B0:C3")
        assertEquals(randomBtEvent.rssi, -92)
        assertEquals(randomBtEvent.timestamp, 24439885559346)

        assertTrue(events[7] is SDKWifiScanResultEvent)
        val randomWifiEvent = events[7] as SDKWifiScanResultEvent
        assertEquals(randomWifiEvent.macAddr.size, 193)
        assertEquals(randomWifiEvent.macAddr[1], 10133350838403861)
        assertEquals(randomWifiEvent.millitime, 1509029544451)
        assertEquals(randomWifiEvent.nanotime, 24445396708856)
        assertEquals(randomWifiEvent.rss.size, 193)
        assertEquals(randomWifiEvent.rss[1], -55)
        assertEquals(randomWifiEvent.scanSize, 193)
        assertEquals(randomWifiEvent.scanStartTime, 1509029540451)
        assertEquals(randomWifiEvent.strMacAddr.size, 193)
        assertEquals(randomWifiEvent.strMacAddr[1], "24:00:3a:99:1b:83:15")
    }

    private fun getFile(path: String): File? {
        val url = EventsParserTest::class.java.getResource(path)
        return File(url.toURI())
    }
}