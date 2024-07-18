package codesquad;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static csv.CsvUtils.*;

public class CsvTest {

    @Test
    void readCsv() {
        String s = "안녕하세요, 하하 hi 1234 \n 하하";
        String encode = encode(s);
        System.out.println(encode);

        String decode = decode(encode);

        System.out.println(decode);

        Assertions.assertEquals(s, decode);
    }
}
