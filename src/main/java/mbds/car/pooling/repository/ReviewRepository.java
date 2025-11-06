package mbds.car.pooling.repository;

import mbds.car.pooling.model.Review;
import mbds.car.pooling.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    
    // Vérifier si un utilisateur a déjà noté un conducteur
    Optional<Review> findByReviewerAndDriver(User reviewer, User driver);
    
    // Compter les reviews et calculer la moyenne pour un conducteur
    @Query("SELECT COUNT(r) FROM Review r WHERE r.driver.uid = :driverId")
    Long countByDriverId(@Param("driverId") String driverId);
    
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.driver.uid = :driverId")
    Double getAverageRatingByDriverId(@Param("driverId") String driverId);
    
    // Vérifier si l'utilisateur existe et est conducteur
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.uid = :userId AND 'DRIVER' MEMBER OF u.roles")
    boolean isUserDriver(@Param("userId") String userId);
}