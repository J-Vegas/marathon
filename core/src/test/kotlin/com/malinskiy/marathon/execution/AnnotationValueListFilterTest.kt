package com.malinskiy.marathon.execution

import com.malinskiy.marathon.test.MetaProperty
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test
import com.malinskiy.marathon.test.Test as MarathonTest

class AnnotationValueListFilterTest {

    private val ann1 = MetaProperty("CaseId", mapOf("value" to listOf("123", "456", "789")))
    private val ann2 = MetaProperty("CaseId", mapOf("value" to "123"))
    private val ann3 = MetaProperty("CaseId", mapOf("value" to listOf("456", "789")))
    private val ann4 = MetaProperty("TmsLink", mapOf("value" to listOf("456", "789", "123")))

    val test1 = stubTest("AnnotationOne", "AnnotationTwo")
    val test2 = stubTest("AnnotationTwo")
    val test3 = stubTest(ann1)
    val test4 = stubTest(ann2)
    val test5 = stubTest(ann3)
    val test6 = stubTest(ann4)
    val valueListFilter = AnnotationValueListFilter(".*123.*".toRegex())
    val tests = listOf(test1, test2, test3, test4, test5, test6)

    private val union = CompositionFilter(
        listOf(
            AnnotationFilter(".*CaseId.*".toRegex()),
//            AnnotationValueFilter(".*123.*".toRegex()),
            AnnotationValueListFilter(".*123.*".toRegex())
        ),
        CompositionFilter.OPERATION.UNION
    )

    @Test
    fun shouldFilterUnion() {
        union.filter(tests) shouldEqual listOf(test3, test4)
    }

    @Test
    fun shouldFilter() {
        valueListFilter.filter(tests) shouldEqual listOf(test3, test4, test6)
    }

    @Test
    fun shouldFilterNot() {
        valueListFilter.filterNot(tests) shouldEqual listOf(test1, test2, test4)
    }
}

private fun stubTest(vararg annotations: MetaProperty) =
    MarathonTest("com.sample", "SimpleTest", "fakeMethod", listOf(*annotations))

private fun stubTest(vararg annotations: String) =
    MarathonTest("com.sample", "SimpleTest", "fakeMethod", annotations.map { MetaProperty(it) })
