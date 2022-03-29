@JsonInclude(JsonInclude.Include.NON_NULL)
data class Paged<T>(
    val items: Collection<T>,
    val page: Int,
    val pageSize: Int,
    val total: Long,
    val sort: String?,
    val sortDir: Sort.Direction?,
) {
    companion object {
        fun <T> of(items: Collection<T>, total: Long, page: Pageable): Paged<T> =
            Paged(
                items = items,
                page = page.pageNumber,
                pageSize = page.pageSize,
                total = total,
                sort = page.sort.map { it.property }.joinToString(","),
                sortDir = page.sort.map { it.direction }.firstOrNull(),
            )
    }
}

fun <T> Page<T>.toPaged(): Paged<T> =
    Paged(
        items = content,
        page = number,
        pageSize = size,
        total = totalElements,
        sort = sort.map { it.property }.joinToString(","),
        sortDir = sort.map { it.direction }.firstOrNull(),
    )
