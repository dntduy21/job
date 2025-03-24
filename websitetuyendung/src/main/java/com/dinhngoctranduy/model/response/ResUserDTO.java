package com.dinhngoctranduy.model.response;

import com.dinhngoctranduy.util.constant.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResUserDTO {
    private long id;
    private String email;
    private String name;
    private Gender gender;
    private String address;
    private int age;
    private Instant updatedAt;
    private Instant createdAt;
}
