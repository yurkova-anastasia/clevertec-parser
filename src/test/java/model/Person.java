package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    private Animal animal;
    private String name;
    private int age;
    private boolean isStudent;
    private int[] grades;
    private List<String> courses;
    private Map<String, String> address;
}
