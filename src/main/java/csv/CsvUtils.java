package csv;

import java.io.*;
import java.util.*;


public class CsvUtils {

    private String filePath;

    public CsvUtils(String filePath) {
        this.filePath = filePath;
    }

    public List<String[]> readAll() throws IOException {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Skip the header
            br.readLine();
            while ((line = br.readLine()) != null) {
                data.add(line.split(","));
            }
        }
        return data;
    }

    public void writeAll(List<String[]> data) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] row : data) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        }
    }

    public void append(String[] row) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(String.join(",", row));
            bw.newLine();
        }
    }

    public static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes());
    }
    public static String decode(String value) {
        return new String(Base64.getDecoder().decode(value));
    }
}