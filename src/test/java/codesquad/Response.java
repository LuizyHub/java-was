package codesquad;

import java.util.List;
import java.util.Map;

record Response(
        int responseCode,
        String responseMessage,
        Map<String, List<String>> headerFields,
        String responseBody
) { }
