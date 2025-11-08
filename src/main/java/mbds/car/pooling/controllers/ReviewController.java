package mbds.car.pooling.controllers;

import lombok.RequiredArgsConstructor;
import mbds.car.pooling.dto.CreateReviewDto;
import mbds.car.pooling.dto.UpdateReviewDto;
import mbds.car.pooling.dto.UserRatingDto;
import mbds.car.pooling.entities.Review;
import mbds.car.pooling.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody CreateReviewDto createReviewDto) {
        try {
            Review review = reviewService.createReview(createReviewDto);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable UUID id,
            @RequestBody UpdateReviewDto updateReviewDto) {
        try {
            Review review = reviewService.updateReview(id, updateReviewDto);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{id}/rating")
    public ResponseEntity<?> getUserRating(@PathVariable String id) {
        try {
            UserRatingDto rating = reviewService.getUserRating(id);
            return ResponseEntity.ok(rating);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}