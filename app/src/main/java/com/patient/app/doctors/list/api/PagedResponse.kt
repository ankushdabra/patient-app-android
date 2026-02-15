package com.patient.app.doctors.list.api

data class PagedResponse<T>(
    val content: List<T>,
    val pageable: PageableDto,
    val totalPages: Int,
    val totalElements: Int,
    val last: Boolean,
    val numberOfElements: Int,
    val first: Boolean,
    val size: Int,
    val number: Int,
    val sort: SortDto,
    val empty: Boolean
)
