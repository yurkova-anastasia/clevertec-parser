package parser;

import exception.JsonDeserializationException;
import exception.JsonSerializationException;

public class JsonParser {

    private final JsonSerializer jsonSerializer;
    private final JsonDeserializer jsonDeserializer;

    public JsonParser() {
        jsonSerializer = new JsonSerializer();
        jsonDeserializer = new JsonDeserializer();
    }

    public String parseToJson(Object value) throws JsonSerializationException {
        return jsonSerializer.toJson(value);
    }

    public <T> T parseFromJson( Class<T> obj, String json) throws JsonDeserializationException {
        return jsonDeserializer.fromJson(obj, json);
    }

}
