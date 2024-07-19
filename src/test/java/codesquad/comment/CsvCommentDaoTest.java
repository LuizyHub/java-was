package codesquad.comment;

import csv.CsvJdbcDriver;

public class CsvCommentDaoTest extends CommentDaoTest {

    @Override
    protected CommentDao createCommentDao() {
        return new CsvCommentDao(new CsvJdbcDriver(), "/test");
    }

    @Override
    void deleteAll() {
    }
}
