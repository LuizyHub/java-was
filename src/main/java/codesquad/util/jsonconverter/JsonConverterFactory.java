package codesquad.util.jsonconverter;

import java.util.Map;

public class JsonConverterFactory {
    private static final JsonConverterFactory jsonConverterFactory = new JsonConverterFactory();
    private JsonConverterFactory() {}
    public static JsonConverterFactory getInstance() {return jsonConverterFactory;}

    public static JsonConverter getConverter(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        if (obj.getClass().isRecord()) {
            return RecordJsonConverter.getInstance();
        } else if (obj instanceof Map) {
            return MapJsonConverter.getInstance();
        } else {
            throw new IllegalArgumentException("Unsupported object type");
        }
    }

    public static boolean canConvert(Object obj) {
        if (obj == null) {
            return false;
        }
        return obj.getClass().isRecord() || obj instanceof Map;
    }
}
