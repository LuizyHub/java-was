package codesquad.user;

public record ImmutableUser(Long id, String userId, String name, String password) {

    public User toUser() {
        User user = new User();
        user.setId(id);
        user.setUserId(userId);
        user.setNickname(name);
        user.setPassword(password);
        return user;
    }
}
