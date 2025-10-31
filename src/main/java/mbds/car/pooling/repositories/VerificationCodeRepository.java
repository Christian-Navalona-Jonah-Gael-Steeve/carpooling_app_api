package mbds.car.pooling.repositories;

import mbds.car.pooling.entities.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByEmailAndCode(String email, String code);

    // retourne le dernier code (par date d'expiration) pour un email
    Optional<VerificationCode> findTopByEmailOrderByExpiresAtDesc(String email);

    // si tu préfères la version simple (peut retourner l'un des codes si plusieurs)
    Optional<VerificationCode> findByEmail(String email);

    @Query("SELECT v FROM VerificationCode v WHERE v.email = :email AND v.expiresAt > CURRENT_TIMESTAMP ORDER BY v.expiresAt DESC")
    Optional<VerificationCode> findTopValidByEmail(@Param("email") String email);

    void deleteByEmail(String email);
}

