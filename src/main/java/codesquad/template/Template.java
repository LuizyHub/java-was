package codesquad.template;

import java.util.Arrays;

public abstract class Template {

    private static final TemplateLoader templateLoader = new TemplateLoader();

    protected String path = "empty.html";

    protected Object[] args;

    private String render() {
        return templateLoader.loadTemplate(path, args);
    }

    @Override
    public String toString() {
        return render();
    }

    public static class NameBtn extends Template {

        public NameBtn(String userName) {
            this.path = "/nameBtn.html";
            this.args = new Object[] {userName};
        }
    }

    public static class UserLi extends Template {

        public UserLi(String userName) {
            this.path = "/userLi.html";
            this.args = new Object[] {userName};
        }
    }

    public static class UserList extends Template {

        public UserList(UserBtn userBtn , UserLi... userLis) {
            this.path = "/userList.html";
            // join userLis
            String joinedUser = Arrays.stream(userLis).map(Template::toString).reduce("", (a, b) -> a + b);
            this.args = new Object[] {userBtn , joinedUser};
        }
    }

    public static class UserBtn extends Template {

        public UserBtn(NameBtn nameBtn, LogoutBtn logoutBtn) {
            this.path = "/biConsumer.html";
            this.args = new Object[] {nameBtn, logoutBtn};
        }
    }

    public static class LogoutBtn extends Template {

        public LogoutBtn() {
            this.path = "/logoutBtn.html";
        }
    }
}
