package codesquad.board;

public class Board {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String imageUrl;

    public Board() {
    }

    public Board(Long userId, String title, String content, String imageUrl) {
        this.userId = userId;
        this.title = title;
        this.content = content;
    }

    public Board(Long id, Long userId, String title, String content, String imageUrl) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
