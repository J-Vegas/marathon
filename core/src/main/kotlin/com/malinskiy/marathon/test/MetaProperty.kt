package com.malinskiy.marathon.test

data class MetaProperty(val name: String, val values: Map<String, Any?> = emptyMap())

data class MetaPropertyList(val name: String, val values: List<DataValues> = emptyList())

data class DataValues(val values: List<MetaProperty> = emptyList())
