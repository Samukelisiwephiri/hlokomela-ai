package za.co.hlokomela.api.web.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;

public final class ApiDtos {
    private ApiDtos() {
    }

    public record ApiError(Instant timestamp, int status, String error, String message,
                           String path, Map<String, String> fieldErrors) { }

    public record PageResponse<T>(List<T> content, int page, int size, long totalElements,
                                  int totalPages, boolean first, boolean last) {
        public static <T> PageResponse<T> from(Page<T> page) {
            return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isFirst(), page.isLast());
        }
    }
}
