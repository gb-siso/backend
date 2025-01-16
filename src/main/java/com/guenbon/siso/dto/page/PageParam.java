package com.guenbon.siso.dto.page;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageParam {

    @Min(value = 0, message = "page는 0 이상이어야 합니다.")
    private Integer page = 0; // 기본값 설정

    @Min(value = 1, message = "size는 1 이상이어야 합니다.")
    private Integer size = 10; // 기본값 설정

    private String sort = "id"; // 기본값 설정

    // sort 유효성 검증
    @AssertTrue(message = "정렬 방향은 ASC, DESC 중 하나여야 합니다.")
    public boolean isSortValid() {
        if (sort == null || sort.trim().isEmpty()) {
            return true; // sort가 없으면 통과
        }

        String[] parts = sort.split(",");
        if (parts.length == 1) {
            return true; // sort 값이 "id" 형태면 통과
        }

        if (parts.length == 2) {
            String direction = parts[1].trim().toUpperCase();
            return "ASC".equals(direction) || "DESC".equals(direction); // 방향 검증
        }

        return false; // 잘못된 형식
    }

    // Pageable 변환 메서드
    public Pageable toPageable() {
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        boolean isDescending = parts.length == 2 && "DESC".equalsIgnoreCase(parts[1].trim());

        return PageRequest.of(page, size, isDescending ?
                Sort.by(property).descending() :
                Sort.by(property).ascending());
    }

    public static PageParam of(int page, int size, String sort) {
        return new PageParam(page, size, sort);
    }

    @Override
    public String toString() {
        return "PageParam{" +
                "page=" + page +
                ", size=" + size +
                ", sort='" + sort + '\'' +
                '}';
    }
}
