package co.com.anfega.model.userbootcamp;

public class UserBootcamp {
    private Long id;
    private Long userId;
    private Long bootcampId;

    public UserBootcamp() {
    }

    public UserBootcamp(Long userId, Long bootcampId) {
        this.userId = userId;
        this.bootcampId = bootcampId;
    }

    public UserBootcamp(Long id, Long userId, Long bootcampId) {
        this.id = id;
        this.userId = userId;
        this.bootcampId = bootcampId;
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

    public Long getBootcampId() {
        return bootcampId;
    }

    public void setBootcampId(Long bootcampId) {
        this.bootcampId = bootcampId;
    }
}
