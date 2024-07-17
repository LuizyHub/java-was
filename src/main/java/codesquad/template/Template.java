package codesquad.template;

import java.util.Arrays;
import java.util.List;

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
            this.path = "/triConsumer.html";
            this.args = new Object[] {new WriteBoardBtn(), nameBtn, logoutBtn};
        }
    }

    public static class LogoutBtn extends Template {

        public LogoutBtn() {
            this.path = "/logoutBtn.html";
        }
    }

    public static class Post extends Template {

        public Post(String username, String title, String imageUrl, String content, List<Comment> comments, String boardId) {
            this.path = "/post.html";
            imageUrl = "\""+imageUrl+"\"";
            String joinedComments = comments.stream().map(Template::toString).reduce("", (a, b) -> a + b);
            this.args = new Object[] {username, title, imageUrl, content, joinedComments, boardId};
        }
    }

    public static class Comment extends Template {

        public Comment(String username, String content) {
            this.path = "/comment.html";
            this.args = new Object[] {username, content};
        }
    }

    public static class UserIndex extends Template {

        public UserIndex(UserBtn userBtn, List<Post> posts) {
            this.path = "/main.html";
            String joinedPosts = posts.stream().map(Template::toString).reduce("", (a, b) -> a + b);
            this.args = new Object[] {userBtn, joinedPosts};
        }
    }

    public static class NonUserIndex extends Template {

        public NonUserIndex(List<Post> posts) {
            this.path = "/main.html";
            String joinedPosts = posts.stream().map(Template::toString).reduce("", (a, b) -> a + b);
            this.args = new Object[] {new NonUserBtn(), joinedPosts};
        }
    }

    public static class NonUserBtn extends Template {

        public NonUserBtn() {
            this.path = "/biConsumer.html";
            this.args = new Object[] {new LoginBtn(), new RegisterBtn()};
        }
    }

    public static class LoginBtn extends Template {

        public LoginBtn() {
            this.path = "/loginBtn.html";
        }
    }

    public static class RegisterBtn extends Template {

        public RegisterBtn() {
            this.path = "/registerBtn.html";
        }
    }

    public static class WriteBoardBtn extends Template {

        public WriteBoardBtn() {
            this.path = "/writeBoardBtn.html";
        }
    }
}
