package mbds.car.pooling.dto;

import java.time.LocalDateTime;

public class ReviewDto
{
    private String id;
    private String comment;
    private int rating;
    private LocalDateTime createdAt;

    private ReviewerDto reviewer;
    private DriverDto driver;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ReviewerDto getReviewer() {
        return reviewer;
    }

    public void setReviewer(ReviewerDto reviewer) {
        this.reviewer = reviewer;
    }

    public DriverDto getDriver() {
        return driver;
    }

    public void setDriver(DriverDto driver) {
        this.driver = driver;
    }
}
