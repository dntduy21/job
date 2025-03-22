package com.dinhngoctranduy.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Meta {
    private int page;
    private int pageSize;
    //Tổng trang với điều kiện query
    private int pages;
    //Tổng số phần tử
    private long total;
}
