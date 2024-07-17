package server.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ByteUtil {
    private ByteUtil() { }
    public static byte[] readUntil(InputStream in, byte[] delimiter) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int matchIndex = 0;
        int byteRead;
        boolean delimiterFound = false;

        while ((byteRead = in.read()) != -1) {
            if (byteRead == delimiter[matchIndex]) {
                matchIndex++;
                if (matchIndex == delimiter.length) {
                    // Delimiter completely matched, so reset match index and break
                    delimiterFound = true;
                    break;
                }
            } else {
                // Not matching, write matched part to buffer and reset match index
                if (matchIndex > 0) {
                    buffer.write(delimiter, 0, matchIndex);
                    matchIndex = 0;
                }
                buffer.write(byteRead);

                // Check if the current byte matches the first byte of the delimiter
                if (byteRead == delimiter[0]) {
                    matchIndex = 1;
                }
            }
        }

        if (!delimiterFound) {
            // If delimiter is not found, return an empty byte array
            return new byte[0];
        }

        return buffer.toByteArray();
    }
}
