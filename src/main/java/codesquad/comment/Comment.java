package codesquad.comment;

public class Comment {
    private Long id;
    private Long userId;
    private Long boardId;
    private String content;

    public Comment(Long userId, Long boardId,  String content) {
        this.userId = userId;
        this.boardId = boardId;
        this.content = content;
    }

    public Comment(Long id, Long userId, Long boardId, String content) {
        this.id = id;
        this.userId = userId;
        this.boardId = boardId;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
