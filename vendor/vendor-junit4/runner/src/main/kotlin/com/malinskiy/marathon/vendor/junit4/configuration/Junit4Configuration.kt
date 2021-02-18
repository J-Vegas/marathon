package com.malinskiy.marathon.vendor.junit4.configuration

import com.malinskiy.marathon.log.MarathonLogConfigurator
import com.malinskiy.marathon.vendor.VendorConfiguration
import com.malinskiy.marathon.vendor.junit4.JUnit4TestParser
import com.malinskiy.marathon.vendor.junit4.Junit4DeviceProvider
import java.io.File

data class Junit4Configuration(
    val applicationJar: File,
    val testsJar: File,
) : VendorConfiguration {
    override fun logConfigurator(): MarathonLogConfigurator = Junit4LogConfigurator()

    override fun testParser() = JUnit4TestParser()

    override fun deviceProvider() = Junit4DeviceProvider()
}
