package mbds.car.pooling.services;

public interface EmailService {

    void sendVerificationCode(String to, String code);

}
