package codesquad.util.jsonconverter;

import java.util.Map;

public class MapJsonConverter implements JsonConverter {
    private static final MapJsonConverter mapJsonConverter = new MapJsonConverter();
    private MapJsonConverter() {}
    public static MapJsonConverter getInstance() {return mapJsonConverter;}

    @Override
    public byte[] convertToJsonBytes(Object obj) throws Exception {
        if (obj == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        if (!(obj instanceof Map<?,?>)) {
            throw new IllegalArgumentException("Object is not a Map");
        }

        String jsonString = convertMapToJson((Map<?, ?>) obj);
        return jsonString.getBytes();
    }

    public String convertMapToJson(Map<?, ?> map) throws Exception {
        StringBuilder mapJsonBuilder = new StringBuilder();
        mapJsonBuilder.append("{");

        int size = map.size();
        int index = 0;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            mapJsonBuilder.append("\"").append(entry.getKey()).append("\":");

            Object value = entry.getValue();

            if (value instanceof String) {
                mapJsonBuilder.append("\"").append(value).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                mapJsonBuilder.append(value);
            } else if (value.getClass().isRecord()) {
                mapJsonBuilder.append(new String(RecordJsonConverter.getInstance().convertToJsonBytes(value)
                ));
            } else if (value instanceof Map) {
                mapJsonBuilder.append(convertMapToJson((Map<?, ?>) value));
            } else {
                mapJsonBuilder.append("\"").append(value.toString()).append("\"");
            }

            if (index < size - 1) {
                mapJsonBuilder.append(",");
            }
            index++;
        }

        mapJsonBuilder.append("}");
        return mapJsonBuilder.toString();
    }
}