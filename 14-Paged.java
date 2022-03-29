@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public final class Paged<T> {
    private Collection<T> items;
    private int page;
    private int pageSize;
    private long total;
    private String sort;
    private Sort.Direction sortDir;

    public static <T> Paged<T> of(Collection<T> items, long total, Pageable page) {
        var response = new Paged<T>();
        response.items = items;
        response.page = page.getPageNumber();
        response.pageSize = page.getPageSize();
        response.total = total;
        if (page.getSort().isSorted()) {
            response.sort = page.getSort().stream()
                .map(Sort.Order::getProperty)
                .collect(Collectors.joining(","));
            response.sortDir = page.getSort().iterator().next().getDirection();
        }

        return response;
    }

    public static <T> Paged<T> of(Page<T> page) {
        var response = new Paged<T>();
        response.items = page.getContent();
        response.page = page.getNumber();
        response.pageSize = page.getSize();
        response.total = page.getTotalElements();
        if (page.getSort().isSorted()) {
            var order = page.getSort().iterator().next();
            response.sort = order.getProperty();
            response.sortDir = order.getDirection();
        }

        return response;
    }
}

