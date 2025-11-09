package mbds.car.pooling.services;

public interface EmailService {

    void sendVerificationCode(String to, String code);
    void sendVerificationCodeHtml(String to, String code);
    void sendReinitialisationCodeHtml(String to, String code) throws Exception;
}
