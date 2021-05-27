package com.github.lipinskipawel

import com.sun.management.HotSpotDiagnosticMXBean
import java.lang.management.ManagementFactory
import javax.management.MBeanServer

class HeapDumper {
    companion object {
        private const val HOTSPOT_BEAN_NAME: String = "com.sun.management:type=HotSpotDiagnostic";
    }

    private val hotspotMBean: HotSpotDiagnosticMXBean

    init {
        val server: MBeanServer = ManagementFactory.getPlatformMBeanServer()
        this.hotspotMBean = ManagementFactory.newPlatformMXBeanProxy(
                server, HOTSPOT_BEAN_NAME, HotSpotDiagnosticMXBean::class.java)
    }

    fun dumpHeap(fileName: String) {
        try {
            this.hotspotMBean.dumpHeap(fileName, true);
        } catch (re: java.lang.RuntimeException) {
            throw re;
        } catch (exp: java.lang.Exception) {
            throw RuntimeException(exp);
        }
    }
}