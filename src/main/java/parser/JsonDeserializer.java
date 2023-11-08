package parser;

import exception.JsonDeserializationException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonDeserializer {

    private final String numberFormat = "(([-0-9.eE]+)|(null))";
    private final String characterFormat = "(" +
            "([0-9]{1,5})|" +
            "(\"\\\\[uU][0-9a-fA-F]{4}\")|" +
            "(\"?null\"?)|" +
            "(\"\\\\[bfnrt\"]\")|" +
            "(\".\")" +
            ")";
    private final String stringFormat = "\"([^\"]*(\"{2})?[^\"]*)*\"";

    private final String booleanFormat = "(true|false)";
    private final String arrayFormat = "(\\[{%d}).+?(]{%d})";

    public <T> T fromJson(Class<T> clazz, String json) throws JsonDeserializationException {
        try {
            T object = clazz.getDeclaredConstructor().newInstance();
            Map<String, String> fieldsWithValues = deserialezeJson(json);
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldValue = fieldsWithValues.get(field.getName());
                switch (field.getType().getSimpleName()) {
                    case "String", "Character", "char" ->
                            field.set(object, fieldValue.substring(1, fieldValue.length() - 1));
                    case "Byte", "byte" -> field.set(object, Byte.valueOf(fieldValue));
                    case "Short", "short" -> field.set(object, Short.valueOf(fieldValue));
                    case "Integer", "int" -> field.set(object, Integer.valueOf(fieldValue));
                    case "Long", "long" -> field.set(object, Long.valueOf(fieldValue));
                    case "Float", "float" -> field.set(object, Float.valueOf(fieldValue));
                    case "Double", "double" -> field.set(object, Double.valueOf(fieldValue));
                    case "Boolean", "boolean" -> field.set(object, Boolean.valueOf(fieldValue));
                    default -> field.set(object, fromJson(field.getType(), fieldValue));
                }
            }
            return object;
        } catch (Exception e) {
            throw new JsonDeserializationException("Could not parse JSON into object");
        }
    }

    public Map<String, String> deserialezeJson(String json) throws JsonDeserializationException {
        if (json == null) throw new JsonDeserializationException("Json must not be null");
        if (!(json.startsWith("{") && json.endsWith("}"))) {
            throw new JsonDeserializationException("It is not an object");
        }
        json = json.substring(1, json.length() - 1);
        Map<String, String> keyValueMap = new HashMap<>();
        String value = null;
        String name;

        while (!json.isEmpty()) {
            name = getName(json);
            json = json.substring(name.length() + 1);

            if (isNull(json)) {
                value = "null";
            }
            if (isNumber(json.charAt(0))) {
                value = getNumber(json);
            } else if (isString(json.charAt(0))) {
                value = getString(json);
            } else if (isArray(json.charAt(0))) {
                value = getArray(json);
            } else if (isBoolean(json.charAt(0))) {
                value = getBoolean(json);
            } else if (isObject(json.charAt(0))) {
                value = getObject(json);
            }

            int valueLength = value.length() + 1;
            if (json.length() <= valueLength) {
                json = "";
            } else {
                json = json.substring(value.length() + 1);
            }

            keyValueMap.put(normalizeName(name), value);
        }
        return keyValueMap;
    }

    private boolean isNull(String json) {
        return json.startsWith("null");
    }

    private String getObject(String json) throws JsonDeserializationException {
        if ("null".equals(json)) return "null";
        if (json == null || !json.startsWith("{")) throw new JsonDeserializationException("Incorrect: " + json);

        final StringBuilder builder = new StringBuilder();
        int brackets = 0;
        for (char c : json.toCharArray()) {
            if (c == '{') brackets++;
            if (c == '}') brackets--;
            builder.append(c);
            if (brackets == 0) break;
        }

        if (brackets != 0) throw new JsonDeserializationException("Incorrect \"{\", \"}\" count");

        return builder.toString();
    }

    private String getArray(String json) throws JsonDeserializationException {
        long brackets = json.chars().takeWhile(c -> c == '[').count();
        if (brackets < 1) throw new JsonDeserializationException("Brackets must be 1 or more");
        String arrayFormat = String.format(this.arrayFormat, brackets, brackets);
        Pattern arrrayPattern = Pattern.compile(arrayFormat);
        return getValue(arrrayPattern, json);
    }

    public String getBoolean(String json) throws JsonDeserializationException {
        if (json == null) throw new JsonDeserializationException("Must not be null");
        Pattern charPattern = Pattern.compile(this.booleanFormat);
        return getValue(charPattern, json);
    }

    private String getString(String json) throws JsonDeserializationException {
        String result;
        try {
            result = getStringValue(json);
        } catch (JsonDeserializationException e) {
            result = getCharValue(json);
        }
        return result;
    }

    public String getCharValue(String json) throws JsonDeserializationException {
        if (json == null) throw new JsonDeserializationException("Must not be null");
        Pattern charPattern = Pattern.compile(this.characterFormat);
        return getValue(charPattern, json);
    }

    public String getStringValue(String json) throws JsonDeserializationException {
        if (json == null) throw new JsonDeserializationException("Must not be null");
        Pattern stringPattern = Pattern.compile(this.stringFormat);
        return getValue(stringPattern, json);
    }

    private String getNumber(String json) throws JsonDeserializationException {
        if (json == null) throw new JsonDeserializationException("Must not be null");
        Pattern numberPattern = Pattern.compile(this.numberFormat);
        return getValue(numberPattern, json);
    }

    private String getValue(Pattern pattern, String json) throws JsonDeserializationException {
        Matcher matcher = pattern.matcher(json);
        return matcher.results()
                .map(MatchResult::group)
                .findFirst()
                .orElseThrow(() -> new JsonDeserializationException("Format error: " + json));
    }

    private String normalizeName(String s) {
        return s.replaceAll("\"", "");
    }

    private String getName(String json) throws JsonDeserializationException {
        if (!json.startsWith("\"")) throw new JsonDeserializationException("not a name");
        StringBuilder builder = new StringBuilder();
        int quotes = 0;
        for (char c : json.toCharArray()) {
            if (quotes == 2) {
                break;
            }
            builder.append(c);
            if (c == '"') quotes++;
        }

        return builder.toString();
    }

    private boolean isNumber(char c) {
        return c >= '0' && c <= '9' || c == '-';
    }

    private boolean isString(char c) {
        return c == '"';
    }

    private boolean isArray(char c) {
        return c == '[';
    }

    private boolean isObject(char c) {
        return c == '{';
    }

    private boolean isBoolean(char c) {
        return c == 't' || c == 'f';
    }
}