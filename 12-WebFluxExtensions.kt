internal const val DEFAULT_PAGE_SIZE = 20

fun ServerRequest.pageRequest(): PageRequest {
    val page = queryParamOrNull("page")?.toIntOrNull() ?: 0
    val sort = queryParamOrNull("sort")?.split(",") ?: emptyList()
    val req = PageRequest.of(page, DEFAULT_PAGE_SIZE)
    if (sort.isEmpty()) return req
    val sortDir = queryParamOrNull("sortDir")?.let { Sort.Direction.fromString(it) } ?: Sort.Direction.ASC
    return req.withSort(sortDir, *sort.toTypedArray())
}

