package mbds.car.pooling.services.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import mbds.car.pooling.services.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

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

    public void templateSendingCode(String title, String supplContent, String to, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(title);

            // 🎨 HTML stylé avec remplacement correct
            String htmlContent = """
                    <html>
                    <body style="font-family: Arial, sans-serif; background-color: #f5f6fa; padding: 20px;">
                        <div style="max-width: 600px; margin: auto; background-color: white; border-radius: 10px; padding: 20px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                            <h2 style="color: #2f3640; text-align: center;">%s</h2>
                            <p>Bonjour,</p>
                            <p>Voici le code :</p>
                            <div style="text-align: center; margin: 30px 0;">
                                <span style="font-size: 24px; font-weight: bold; color: #e84118; letter-spacing: 2px;">%s</span>
                            </div>
                            <p>%s</p>
                            <br>
                            <p style="font-size: 12px; color: #718093;">Cordialement,<br>L'équipe Carpooling 🚗</p>
                        </div>
                    </body>
                    </html>
                    """.formatted(title, code, supplContent);

            helper.setText(htmlContent, true); // 💡 "true" = HTML activé

            mailSender.send(message);
            System.out.println("✅ Email envoyé à " + to);

        } catch (MessagingException e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }

    public void sendVerificationCodeHtml(String to, String code) {
        String title = "Vérification du compte";
        String suppl_content = "Entrez ce code dans l'application pour confirmer votre compte";
        this.templateSendingCode(title,suppl_content , to, code);
    }

    public void sendReinitialisationCodeHtml(String to, String code) throws Exception {
        // Génère un code à 6 chiffres
        String title = "Réinitialisation de mot de passe de votre compte";
        String suppl_content = "Entrez ce code dans l'application avec votre nouveau mot de passe pour la réinitialisation";
        this.templateSendingCode(title,suppl_content , to, code);
    }
}
