package com.marco.cloud_ecommerce_api.application.common;

import com.marco.cloud_ecommerce_api.application.product.ProductFilterDTO;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponseDTO<T> {

    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;
    private final boolean first;
    private final boolean last;
    private final ProductFilterDTO appliedFilters;

    public PageResponseDTO(Page<T> page) {
        this(page, null);
    }

    public PageResponseDTO(Page<T> page, ProductFilterDTO appliedFilters) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.appliedFilters = appliedFilters;
    }
}
