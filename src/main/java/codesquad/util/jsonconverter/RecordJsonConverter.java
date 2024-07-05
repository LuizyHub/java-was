package codesquad.util.jsonconverter;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Map;

public final class RecordJsonConverter implements JsonConverter {
    private static final RecordJsonConverter instance = new RecordJsonConverter();
    private RecordJsonConverter() {}
    public static RecordJsonConverter getInstance() {return instance;}

    @Override
    public byte[] convertToJsonBytes(Object obj) throws Exception {
        if (obj == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        if (!obj.getClass().isRecord()) {
            throw new IllegalArgumentException("Object is not a record");
        }

        String jsonString = convertRecordToJson(obj);
        return jsonString.getBytes();
    }

    private String convertRecordToJson(Object record) throws Exception {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        RecordComponent[] components = record.getClass().getRecordComponents();
        for (int i = 0; i < components.length; i++) {
            RecordComponent component = components[i];
            Method accessor = component.getAccessor();
            String name = component.getName();
            Object value = accessor.invoke(record);

            jsonBuilder.append("\"").append(name).append("\":");

            if (value instanceof String) {
                jsonBuilder.append("\"").append(value).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                jsonBuilder.append(value);
            } else if (value.getClass().isRecord()) {
                jsonBuilder.append(convertRecordToJson(value));
            } else if (value instanceof Map<?,?>) {
                jsonBuilder.append(MapJsonConverter.getInstance().convertMapToJson((Map<?, ?>) value));
            } else {
                jsonBuilder.append("\"").append(value.toString()).append("\"");
            }

            if (i < components.length - 1) {
                jsonBuilder.append(",");
            }
        }

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }
}

