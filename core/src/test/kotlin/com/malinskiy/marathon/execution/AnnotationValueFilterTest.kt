package com.malinskiy.marathon.execution

import com.malinskiy.marathon.test.MetaProperty
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test
import com.malinskiy.marathon.test.Test as MarathonTest

class AnnotationValueFilterTest {

    class MetaPropertyValue(val name: String, val value: String)

    private val mPv1 = MetaPropertyValue("Annotation", "12345")
    private val mPv2 = MetaPropertyValue("Annotation", "54321")
    private val mPv3 = MetaPropertyValue("Annotation", "45123")

    private val annotation1 = MetaProperty("AnnotationOne", mapOf("Annotation" to "12345"))
    private val annotation2 = MetaProperty("AnnotationOne", mapOf("Annotation" to "54321"))
    private val annotation3 = MetaProperty("AnnotationOne", mapOf("Annotation" to "45123", "Annotation" to "12345"))

    val test1 = stubTest("AnnotationOne", "AnnotationTwo")
    val test2 = stubTest("AnnotationTwo")
    val test3 = stubTest(annotation1)
    val test4 = stubTest(annotation2)
    val test5 = stubTest(annotation3)
    val valueFilter = AnnotationValueFilter(".*12345.*".toRegex())
    val tests = listOf(test1, test2, test3, test4, test5)

    private val union = CompositionFilter(
        listOf(
            AnnotationFilter(".*Annotation.*".toRegex()),
            valueFilter
        ),
        CompositionFilter.OPERATION.UNION
    )

    @Test
    fun shouldFilterUnion() {
        union.filter(tests) shouldEqual listOf(test3)
    }

    @Test
    fun shouldFilter() {
        valueFilter.filter(tests) shouldEqual listOf(test3, test5)
    }

    @Test
    fun shouldFilterNot() {
        valueFilter.filterNot(tests) shouldEqual listOf(test1, test2, test4)
    }
}

private fun stubTest(vararg annotations: MetaProperty) = MarathonTest("com.sample", "SimpleTest", "fakeMethod", listOf(*annotations))
private fun stubTest(vararg annotations: String) =
    MarathonTest("com.sample", "SimpleTest", "fakeMethod", annotations.map { MetaProperty(it) })
