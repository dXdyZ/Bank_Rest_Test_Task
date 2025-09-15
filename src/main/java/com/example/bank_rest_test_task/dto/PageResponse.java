package com.example.bank_rest_test_task.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Page response")
public class PageResponse<T> {
    @ArraySchema(
            schema = @Schema(
                    oneOf = {CardDto.class, CardBlockRequestDto.class, UserDto.class},
                    description = "The items can be users cards and card blocking requests"
            )
    )
    private List<T> content;
    @Schema(description = "Total elements", example = "55")
    private long totalElements;
    @Schema(description = "Total pages", example = "10")
    private int totalPages;
    @Schema(description = "Number of the received page", example = "1")
    private int page;
    @Schema(description = "How many elements are on the page", example = "5")
    private int size;
    @Schema(description = "The current page is the first one", example = "true")
    private boolean first;
    @Schema(description = "The current page is the last one", example = "false")
    private boolean last;

    /**
     * Превращает {@link Page} в кастомный {@link PageResponse}
     *
     * @param page объект для превращения
     * @return превращенный результат
     * @param <T> объекты, которые лежат в результате
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.isFirst(),
                page.isLast()
        );
    }
}
