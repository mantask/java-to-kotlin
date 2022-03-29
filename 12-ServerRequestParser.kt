public final class ServerRequestParser {
    public static final int DEFAULT_PAGE_SIZE = 20;

    public PageRequest parsePageRequest(ServerRequest request) {
        var pageRequest = PageRequest.of(
            request.queryParam("page")
                .map(Integer::valueOf)
                .orElse(0),
            DEFAULT_PAGE_SIZE);
        var sortProps = request.queryParam("sort")
            .map(sort -> sort.split(","))
            .orElse(new String[]{});
        if (sortProps.length > 0) pageRequest = pageRequest.withSort(
            request.queryParam("sortDir")
                .map(Sort.Direction::fromString)
                .orElse(Sort.Direction.ASC),
            sortProps);
        return pageRequest;
    }
}

