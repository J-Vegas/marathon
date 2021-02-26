package com.malinskiy.marathon.execution

import com.malinskiy.marathon.test.MetaProperty
import com.malinskiy.marathon.test.MetaPropertyList
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test
import com.malinskiy.marathon.test.Test as MarathonTest

class AnnotationValueListFilterTest {

    private val annotation1 = MetaProperty("AnnotationOne", mapOf("Annotation" to "12345"))
    private val annotation2 = MetaProperty("AnnotationOne", mapOf("Annotation" to "54321"))
    private val annotation3 = MetaProperty("AnnotationOne", mapOf("Annotation" to "45123", "Annotation" to "12345"))
    private val annotation4 = MetaPropertyList("Annotations", listOf(annotation1))
    private val annotation5 = MetaPropertyList("Annotations", listOf(annotation2))
    private val annotation6 = MetaPropertyList("Annotations", listOf(annotation1, annotation2))

    val test1 = stubTest("AnnotationOne", "AnnotationTwo")
    val test2 = stubTest("AnnotationTwo")
    val test3 = stubTest(annotation4)
    val test4 = stubTest(annotation5)
    val test5 = stubTest(annotation6)
    val valueFilter = AnnotationValueListFilter(".*12345.*".toRegex())
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

private fun stubTest(vararg annotations: MetaPropertyList) = MarathonTest("com.sample", "SimpleTest", "fakeMethod", metaPropertiesList =  listOf(*annotations))
private fun stubTest(vararg annotations: String) =
    MarathonTest("com.sample", "SimpleTest", "fakeMethod", annotations.map { MetaProperty(it) })
