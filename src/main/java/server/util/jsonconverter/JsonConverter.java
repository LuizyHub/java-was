package server.util.jsonconverter;

public interface JsonConverter {
    byte[] convertToJsonBytes(Object obj) throws Exception;
}
