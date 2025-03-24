package com.dinhngoctranduy.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPaginationDTO {
    private Meta meta;
    private Object result;

    @Getter
    @Setter
    public static class Meta {
        private int page;
        private int pageSize;
        //Tổng trang với điều kiện query
        private int pages;
        //Tổng số phần tử
        private long total;
    }
}
