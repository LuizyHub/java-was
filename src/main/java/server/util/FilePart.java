package server.util;

public record FilePart(String name, String fileName, String contentType, byte[] data) {
}
