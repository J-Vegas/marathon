package com.malinskiy.marathon.ios.idb

import com.malinskiy.marathon.execution.Configuration
import com.malinskiy.marathon.execution.TestParser
import com.malinskiy.marathon.test.Test

class IdbTestParser : TestParser {
    override fun extract(configuration: Configuration): List<Test> {
        return emptyList()
    }
}
