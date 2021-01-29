package com.malinskiy.marathon.execution.strategy

import com.malinskiy.marathon.analytics.external.Analytics
import com.malinskiy.marathon.test.Test
import com.malinskiy.marathon.test.TestBatch
import java.time.Instant
import java.util.*

sealed class BatchingStrategy {
    abstract fun process(queue: Queue<Test>, analytics: Analytics): TestBatch
}

data class FixedSizeBatchingStrategy(
    private val size: Int,
    private val durationMillis: Long? = null,
    private val percentile: Double? = null,
    private val timeLimit: Instant? = null,
    private val lastMileLength: Int = 0
) : BatchingStrategy() {
    override fun process(queue: Queue<Test>, analytics: Analytics): TestBatch {
        if (queue.size < lastMileLength && queue.isNotEmpty()) {
            //We optimize last mile by disabling batching completely.
            // This allows us to parallelize the test runs at the end instead of running batches in series
            return TestBatch(listOf(queue.poll()))
        }

        var counter = 0
        var expectedBatchDuration = 0.0
        val duplicates = mutableListOf<Test>()
        val result = mutableSetOf<Test>()
        while (counter < size && queue.isNotEmpty()) {
            counter++
            val item = queue.poll()
            if (result.contains(item)) {
                duplicates.add(item)
            } else {
                result.add(item)
            }

            if (durationMillis != null && percentile != null && timeLimit != null) {
                //Check for expected batch duration. If we hit the duration limit - break
                //Important part is to add at least one test so that if one test is longer than a batch
                //We still have at least one test
                val expectedTestDuration = analytics.metricsProvider.executionTime(item, percentile, timeLimit)
                expectedBatchDuration += expectedTestDuration
                if (expectedBatchDuration >= durationMillis) break
            }
        }
        if (duplicates.isNotEmpty()) {
            queue.addAll(duplicates)
        }
        return TestBatch(result.toList())
    }
}

class IsolateBatchingStrategy : BatchingStrategy() {
    override fun process(queue: Queue<Test>, analytics: Analytics): TestBatch = TestBatch(listOf(queue.poll()))
}
