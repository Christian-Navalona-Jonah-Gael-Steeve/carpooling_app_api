package mbds.car.pooling.service;

import lombok.RequiredArgsConstructor;
import mbds.car.pooling.dto.CreateReviewDto;
import mbds.car.pooling.dto.UpdateReviewDto;
import mbds.car.pooling.dto.UserRatingDto;
import mbds.car.pooling.enums.UserRole;
import mbds.car.pooling.entities.Review;
import mbds.car.pooling.entities.User;
import mbds.car.pooling.repositories.ReviewRepository;
import mbds.car.pooling.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public Review createReview(CreateReviewDto createReviewDto) {
        // Validation du rating
        if (createReviewDto.getRating() < 0 || createReviewDto.getRating() > 5) {
            throw new IllegalArgumentException("Le rating doit être entre 0 et 5");
        }

        // Vérifier que le reviewer existe
        User reviewer = userRepository.findById(createReviewDto.getReviewerId().toString())
                .orElseThrow(() -> new RuntimeException("Utilisateur reviewer non trouvé"));

        // Vérifier que le driver existe et a le rôle DRIVER
        User driver = userRepository.findById(createReviewDto.getDriverId().toString())
                .orElseThrow(() -> new RuntimeException("Conducteur non trouvé"));

        if (!driver.getRoles().contains(UserRole.DRIVER)) {
            throw new IllegalArgumentException("L'utilisateur spécifié n'est pas un conducteur");
        }

        // Vérifier que le reviewer n'est pas le driver
        if (reviewer.getUid().equals(driver.getUid())) {
            throw new IllegalArgumentException("Un utilisateur ne peut pas se noter lui-même");
        }

        // Vérifier si l'utilisateur a déjà noté ce conducteur
        reviewRepository.findByReviewerAndDriver(reviewer, driver)
                .ifPresent(review -> {
                    throw new IllegalArgumentException("Vous avez déjà noté ce conducteur");
                });

        // Créer la review
        Review review = new Review();
        review.setReviewer(reviewer);
        review.setDriver(driver);
        review.setRating(createReviewDto.getRating());

        return reviewRepository.save(review);
    }

    public Review updateReview(UUID reviewId, UpdateReviewDto updateReviewDto) {
        // Validation du rating
        if (updateReviewDto.getRating() < 0 || updateReviewDto.getRating() > 5) {
            throw new IllegalArgumentException("Le rating doit être entre 0 et 5");
        }

        // Récupérer la review
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review non trouvée"));

        // Mettre à jour le rating
        review.setRating(updateReviewDto.getRating());

        return reviewRepository.save(review);
    }

    public UserRatingDto getUserRating(String userId) {
        // Vérifier que l'utilisateur existe
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        Long reviewsCount = reviewRepository.countByDriverId(userId);
        Double avgRating = reviewRepository.getAverageRatingByDriverId(userId);

        // Arrondir la moyenne à 2 décimales
        double roundedAvg = Math.round(avgRating * 100.0) / 100.0;

        return new UserRatingDto(reviewsCount, roundedAvg);
    }
}