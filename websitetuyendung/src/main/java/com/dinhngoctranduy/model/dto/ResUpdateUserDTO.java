package com.dinhngoctranduy.model.dto;

import com.dinhngoctranduy.util.constant.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private Gender gender;
    private String address;
    private int age;
    private Instant updatedAt;
}
