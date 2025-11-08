package mbds.car.pooling.service;

import lombok.RequiredArgsConstructor;
import mbds.car.pooling.dto.*;
import mbds.car.pooling.enums.UserRole;
import mbds.car.pooling.entities.Review;
import mbds.car.pooling.entities.User;
import mbds.car.pooling.repositories.ReviewRepository;
import mbds.car.pooling.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional
    public Review createReview(CreateReviewDto createReviewDto) {
        try {
            if (createReviewDto.getRating() == null || createReviewDto.getRating() < 0 || createReviewDto.getRating() > 5) {
                throw new IllegalArgumentException("Le rating doit être entre 0 et 5");
            }

            if (createReviewDto.getReviewerId() == null || createReviewDto.getReviewerId().trim().isEmpty()) {
                throw new IllegalArgumentException("L'ID du reviewer est requis");
            }

            if (createReviewDto.getDriverId() == null || createReviewDto.getDriverId().trim().isEmpty()) {
                throw new IllegalArgumentException("L'ID du conducteur est requis");
            }

            User reviewer = userRepository.findById(createReviewDto.getReviewerId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur reviewer non trouvé"));

            User driver = userRepository.findById(createReviewDto.getDriverId())
                    .orElseThrow(() -> new RuntimeException("Conducteur non trouvé"));

            if (!driver.getRoles().contains(UserRole.DRIVER)) {
                throw new IllegalArgumentException("L'utilisateur spécifié n'est pas un conducteur");
            }

            // Vérifier que le reviewer n'est pas le driver
            if (reviewer.getUid().equals(driver.getUid())) {
                throw new IllegalArgumentException("Un utilisateur ne peut pas se noter lui-même");
            }

            Optional<Review> existingReview = reviewRepository.findByReviewerAndDriver(reviewer, driver);
            if (existingReview.isPresent()) {
                throw new IllegalArgumentException("Vous avez déjà noté ce conducteur");
            }

            Review review = new Review();
            review.setReviewer(reviewer);
            review.setDriver(driver);
            review.setRating(createReviewDto.getRating());
            review.setComment(createReviewDto.getComment());

            return reviewRepository.save(review);
        } catch (Exception e) {
            System.err.println("Erreur création review: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public Review updateReview(UUID reviewId, UpdateReviewDto updateReviewDto) {
        if (updateReviewDto.getRating() < 0 || updateReviewDto.getRating() > 5) {
            throw new IllegalArgumentException("Le rating doit être entre 0 et 5");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review non trouvée"));

        review.setRating(updateReviewDto.getRating());
        review.setComment(updateReviewDto.getComment()); // Mise à jour du commentaire

        return reviewRepository.save(review);
    }

    public UserRatingDto getUserRating(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        Long reviewsCount = reviewRepository.countByDriverId(userId);
        Double avgRating = reviewRepository.getAverageRatingByDriverId(userId);

        double roundedAvg = Math.round(avgRating * 100.0) / 100.0;

        return new UserRatingDto(reviewsCount, roundedAvg);
    }

    public List<Review> getDriverReviews(String driverId) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Conducteur non trouvé"));

        return reviewRepository.findByDriver(driver);
    }

    public Optional<Review> getUserReviewForDriver(String driverId, String reviewerId) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Conducteur non trouvé"));
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("Reviewer non trouvé"));

        return reviewRepository.findByReviewerAndDriver(reviewer, driver);
    }

    public ReviewDto toDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId().toString());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());

        if (review.getReviewer() != null) {
            ReviewerDto r = new ReviewerDto();
            r.setUid(review.getReviewer().getUid());
            r.setFirstName(review.getReviewer().getFirstName());
            r.setLastName(review.getReviewer().getLastName());
            r.setEmail(review.getReviewer().getEmail());
            dto.setReviewer(r);
        }

        if (review.getDriver() != null) {
            DriverDto d = new DriverDto();
            d.setUid(review.getDriver().getUid());
            d.setFirstName(review.getDriver().getFirstName());
            d.setLastName(review.getDriver().getLastName());
            d.setEmail(review.getDriver().getEmail());
            dto.setDriver(d);
        }

        return dto;
    }
}