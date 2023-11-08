package parser;

import com.google.gson.Gson;
import exception.JsonDeserializationException;
import exception.JsonSerializationException;
import model.Animal;
import model.Order;
import model.Person;
import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonParserTest {

    private  JsonParser jsonParser;
    private Gson gson;

    @BeforeEach
    void setUp() {
        jsonParser = new JsonParser();
        gson = new Gson();
    }

    @Test
    void toJson_withNullObject_shouldReturnNull() throws JsonSerializationException {
        // Given
        Object object = null;
        String expected = gson.toJson(object);

        // When
        String actual = jsonParser.parseToJson(object);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void toJson_withInteger_shouldReturnNumberAsJson() throws JsonSerializationException {
        // Given
        Integer object = 42;
        String expected = gson.toJson(object);

        // When
        String actual = jsonParser.parseToJson(object);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void toJson_withString_shouldReturnStringAsJson() throws JsonSerializationException {
        // Given
        String object = "Hello, JSON";
        String expected = gson.toJson(object);

        // When
        String actual = jsonParser.parseToJson(object);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void toJson_withBoolean_shouldReturnBooleanAsJson() throws JsonSerializationException {
        // Given
        Boolean object = true;
        String expected = gson.toJson(object);

        // When
        String actual = jsonParser.parseToJson(object);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void toJson_withDate_shouldReturnDateAsJson() throws JsonSerializationException {
        // Given
        Date date = new Date();
        String expected = "\"" + date + "\"";

        // When
        String actual = jsonParser.parseToJson(date);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void toJson_withLocalDateTime_shouldReturnLocalDateTimeAsJson() throws JsonSerializationException {
        // Given
        LocalDateTime localDateTime = LocalDateTime.now();
        String expected = "\"" + localDateTime + "\"";

        // When
        String actual = jsonParser.parseToJson(localDateTime);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void toJson_withArray_shouldReturnArrayAsJson() throws JsonSerializationException {
        // Given
        int[] array = new int[3];
        array[0] = 2;
        array[1] = 1;
        array[2] = 4;

        String expected = gson.toJson(array);

        // When
        String actual = jsonParser.parseToJson(array);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void toJson_withNestedArrays_shouldReturnArrayAsJson() throws JsonSerializationException {
        // Given
        int[][] array = new int[3][];
        int[] nestedArray = new int[3];
        nestedArray[0] = 1;
        nestedArray[1] = 2;
        nestedArray[2] = 3;
        array[0] = nestedArray;
        array[1] = nestedArray;
        array[2] = nestedArray;

        String expected = gson.toJson(array);

        // When
        String actual = jsonParser.parseToJson(array);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void toJson_withMap_shouldReturnLocalDateTimeAsJson() throws JsonSerializationException {
        // Given
        Map<String, Object> map = new HashMap<>();
        map.put("name", "John");
        map.put("age", 30);
        map.put("isStudent", false);

        List<String> hobbies = Arrays.asList("Reading", "Gardening");
        map.put("hobbies", hobbies);


        String expected = gson.toJson(map);

        // When
        String actual = jsonParser.parseToJson(map);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void toJson_withCustomObject_shouldSerializeObjectFields() throws JsonSerializationException {
        // Given
        Order order = new Order();
        Person person = new Person();
        person.setAge(20);
        person.setName("nastya");
        int[] grades = new int[3];
        grades[0] = 2;
        grades[1] = 1;
        grades[2] = 4;
        person.setGrades(grades);
        person.setCourses(List.of("math", "lit"));
        Map<String, String> countryAndTow = new HashMap<>();
        countryAndTow.put("Belarus", "Minsk");
        countryAndTow.put("Russia", "Moscow");
        person.setAddress(countryAndTow);
        person.setStudent(true);
        Animal animal = new Animal();
        animal.setId(123);
        animal.setName("Лев");
        animal.setAge(5);
        animal.setPrice(1500.99);
        animal.setIsIll(true);
        person.setAnimal(animal);
        order.setPerson(person);
        List<Product> products = new ArrayList<>();
        products.add(new Product("Product 1", BigDecimal.valueOf(19.99), false, countryAndTow));
        order.setProducts(products);

        String expected = gson.toJson(order);

        // When
        String actual = jsonParser.parseToJson(order);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    public void fromJson_withNullObject_shouldThrowJsonDeserializationException() {
        // Given
        String json = "";

        // Then
        assertThrows(JsonDeserializationException.class,
                () -> jsonParser.parseFromJson(Object.class, json), "Json must not be null");
    }

    @Test
    public void testDeserializeObject() throws JsonDeserializationException {
        // Given
        String json = "{\"id\":20,\"name\":\"Bobik\",\"age\":5,\"price\":33.33,\"isIll\":true}";
        Animal expected = gson.fromJson(json, Animal.class);

        // When
        Animal actual = jsonParser.parseFromJson(Animal.class, json);

        // Then
        assertEquals(expected, actual);
    }
}