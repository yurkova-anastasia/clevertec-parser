package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Animal {

    private long id;
    private String name;
    private Integer age;
    private double price;
    private Boolean isIll;
}
