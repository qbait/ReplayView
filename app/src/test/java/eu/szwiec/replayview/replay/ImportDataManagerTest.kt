package eu.szwiec.replayview.replay

import eu.szwiec.replayview.otto.EddystoneUidPacketEvent
import org.junit.Test

import java.io.IOException

import eu.szwiec.replayview.otto.SDKWifiScanResultEvent

import org.junit.Assert.*
import java.io.File

class ImportDataManagerTest {

    @Test
    fun importWifi() {
        val path = "/replay/sample.wifi0"
        val expectedSize = 146
        var events = emptyList<SDKWifiScanResultEvent>()

        try {
            events = ImportDataManager.getWifiEvents( getFile(path) )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        assertNotNull(events)
        assertEquals(expectedSize, events.size)
        assertTrue(events.stream().allMatch { e -> e is SDKWifiScanResultEvent })
    }

    @Test
    fun importBluetooth() {
        val path = "/replay/sample.edyuid0"
        val expectedSize = 222
        var events = emptyList<EddystoneUidPacketEvent>()

        try {
            events = ImportDataManager.getBluetoothEvents( getFile(path) )
        } catch (e: IOException) {
            e.printStackTrace()
        }

        assertNotNull(events)
        assertEquals(expectedSize, events.size)
        assertTrue(events.stream().allMatch { e -> e is EddystoneUidPacketEvent })
    }

    private fun getFile(path: String): File? {
        val url = ImportDataManagerTest::class.java.getResource(path)
        return File(url.toURI())
    }

}