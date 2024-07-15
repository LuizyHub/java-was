package codesquad.user;

class MemoryUserDaoTest extends UserDaoTest {

    @Override
    protected UserDao createUserDao() {
        return new MemoryUserDao();
    }
}