package mbds.car.pooling.services.impl;

import lombok.RequiredArgsConstructor;
import mbds.car.pooling.services.AuthService;
import mbds.car.pooling.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Vérification de votre compte");
        message.setText("Bonjour,\n\nVotre code de vérification est : " + code + "\n\nL'équipe.");
        mailSender.send(message);
    }
}
