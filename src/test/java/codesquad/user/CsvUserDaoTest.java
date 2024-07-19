package codesquad.user;

import csv.CsvJdbcDriver;

public class CsvUserDaoTest extends UserDaoTest {

    @Override
    protected UserDao createUserDao() {
        return new CsvUserDao(new CsvJdbcDriver(), "/test");
    }
}
