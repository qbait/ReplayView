package eu.szwiec.replayview.replay

import eu.szwiec.replayview.otto.EddystoneUidPacketEvent
import eu.szwiec.replayview.otto.SDKWifiScanResultEvent
import junit.framework.Assert.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class ReplayEventsParserTest {

    @Test
    fun getEvents() {
        val btFile = ReplayFile(openAsset("sample.edyuid0"), ReplayType.BLUETOOTH)
        val wifiFile = ReplayFile(openAsset("sample.wifi0"), ReplayType.WIFI)

        val expectedSize = 368

        val events = ReplayEventsParser.getEvents(listOf(btFile, wifiFile))

        assertEquals(expectedSize, events.size)
        assertTrue(events.stream().allMatch { e -> e is ReplayEvent })

        events.sortBy { it.nanoTimestamp }

        assertTrue(events[2] is EddystoneUidPacketEvent)
        val randomBtEvent = events[2] as EddystoneUidPacketEvent
        assertTrue(randomBtEvent is EddystoneUidPacketEvent)
        assertEquals("CB:3B:9B:41:B0:C3", randomBtEvent.deviceAddress)
        assertEquals(-92, randomBtEvent.rssi)
        assertEquals(24439885559346, randomBtEvent.timestamp)

        assertTrue(events[7] is SDKWifiScanResultEvent)
        val randomWifiEvent = events[7] as SDKWifiScanResultEvent
        assertEquals(193, randomWifiEvent.macAddr.size)
        assertEquals(10133350838403861, randomWifiEvent.macAddr[1])
        assertEquals(1509029544451, randomWifiEvent.millitime)
        assertEquals(24445396708856, randomWifiEvent.nanotime)
        assertEquals(193, randomWifiEvent.rss.size)
        assertEquals(-55, randomWifiEvent.rss[1])
        assertEquals(193, randomWifiEvent.scanSize)
        assertEquals(1509029540451, randomWifiEvent.scanStartTime)
        assertEquals(193, randomWifiEvent.strMacAddr.size)
        assertEquals("24:00:3a:99:1b:83:15", randomWifiEvent.strMacAddr[1])
    }

}