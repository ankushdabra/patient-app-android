package com.patient.app.doctors.list.api

data class PageableDto(
    val pageNumber: Int,
    val pageSize: Int,
    val sort: SortDto,
    val offset: Long,
    val paged: Boolean,
    val unpaged: Boolean
)
