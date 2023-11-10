package parser;

import exception.JsonSerializationException;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class JsonSerializer {

    public String toJson(Object object) throws JsonSerializationException {
        try {
            StringBuilder json = new StringBuilder();
            serializeObject(json, object);
            return json.toString();
        } catch (Exception e) {
            throw new JsonSerializationException("Error serializing Object: " + e.getMessage(), e);
        }
    }

    private void serializeObject(StringBuilder json, Object object) throws JsonSerializationException {
        try {
            if (object == null) {
                json.append("null");
            } else if (object instanceof Number || object instanceof Boolean) {
                json.append(object);
            } else if (object instanceof String) {
                json.append("\"").append(escapeString((String) object)).append("\"");
            } else if (object instanceof Collection<?>) {
                serializeCollection(json, (Collection<?>) object);
            } else if (object instanceof Map<?, ?>) {
                serializeMap(json, (Map<?, ?>) object);
            } else if (object.getClass().isArray()) {
                serializeArray(json, object);
            } else if (object instanceof Date || object instanceof LocalDateTime
                    || object instanceof LocalTime || object instanceof LocalDate
                    || object instanceof OffsetDateTime) {
                json.append("\"").append(object).append("\"");
            } else {
                serializeCustomObject(json, object);
            }
        } catch (JsonSerializationException e) {
            throw new JsonSerializationException("Error serializing Object: " + e.getMessage(), e);
        }
    }

    private void serializeCollection(StringBuilder json, Collection<?> collection) throws JsonSerializationException {
        try {
            json.append("[");
            boolean first = true;
            for (Object item : collection) {
                if (!first) {
                    json.append(",");
                }
                serializeObject(json, item);
                first = false;
            }
            json.append("]");
        } catch (JsonSerializationException e) {
            throw new JsonSerializationException("Error serializing Object: " + e.getMessage(), e);
        }
    }

    private void serializeMap(StringBuilder json, Map<?, ?> map) throws JsonSerializationException {
        try {
            json.append("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) {
                    json.append(",");
                }
                json.append("\"").append(escapeString(entry.getKey().toString())).append("\":");
                serializeObject(json, entry.getValue());
                first = false;
            }
            json.append("}");
        } catch (JsonSerializationException e) {
            throw new JsonSerializationException("Error serializing Object: " + e.getMessage(), e);
        }
    }

    private void serializeArray(StringBuilder json, Object array) throws JsonSerializationException {
        try {
            json.append("[");
            boolean first = true;

            int length = Array.getLength(array);
            for (int i = 0; i < length; i++) {
                if (!first) {
                    json.append(",");
                }
                Object item = Array.get(array, i);
                serializeObject(json, item);
                first = false;
            }

            json.append("]");
        } catch (JsonSerializationException e) {
            throw new JsonSerializationException("Error serializing Object: " + e.getMessage(), e);
        }
    }

    private void serializeCustomObject(StringBuilder json, Object object) throws JsonSerializationException {
        try {
            json.append("{");
            Field[] fields = object.getClass().getDeclaredFields();
            boolean first = true;

            for (Field field : fields) {
                if (!first) {
                    json.append(",");
                }
                json.append("\"").append(field.getName()).append("\":");

                field.setAccessible(true);
                Object fieldValue = field.get(object);
                serializeObject(json, fieldValue);

                first = false;
            }

            json.append("}");
        } catch (Exception e) {
            throw new JsonSerializationException("Error serializing Object: " + e.getMessage(), e);
        }
    }

    private String escapeString(String s) {
        StringBuilder escaped = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (Character.isISOControl(c)) {
                        escaped.append("\\u");
                        escaped.append(String.format("%04x", (int) c));
                    } else {
                        escaped.append(c);
                    }
                    break;
            }
        }
        return escaped.toString();
    }

}
