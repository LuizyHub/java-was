package codesquad.board;

import csv.CsvJdbcDriver;

public class CsvBoardDaoTest extends BoardDaoTest {

    @Override
    protected BoardDao createBoardDao() {
        return new CsvBoardDao(new CsvJdbcDriver());
    }

    @Override
    void deleteAll() {
        // do nothing
    }
}
