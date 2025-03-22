package com.dinhngoctranduy.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestResponse<T> {
    private int statusCode;
    private String error;

    //message có thể là String hoặc arraylist
    private Object message;
    private T data;
}
