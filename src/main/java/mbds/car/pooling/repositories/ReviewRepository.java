package mbds.car.pooling.repositories;

import mbds.car.pooling.entities.Review;
import mbds.car.pooling.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID>
{
    Optional<Review> findByReviewerAndDriver(User reviewer, User driver);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.driver.uid = :driverId")
    Long countByDriverId(@Param("driverId") String driverId);
    
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.driver.uid = :driverId")
    Double getAverageRatingByDriverId(@Param("driverId") String driverId);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.uid = :userId AND 'DRIVER' MEMBER OF u.roles")
    boolean isUserDriver(@Param("userId") String userId);

    List<Review> findByDriver(User driver);

    @Query("SELECT r FROM Review r JOIN FETCH r.reviewer JOIN FETCH r.driver WHERE r.id = :id")
    Optional<Review> findWithUsersById(UUID id);
}