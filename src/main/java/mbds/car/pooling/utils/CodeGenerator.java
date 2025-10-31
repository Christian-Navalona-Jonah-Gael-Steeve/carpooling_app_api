package mbds.car.pooling.utils;

import java.security.SecureRandom;

public class CodeGenerator {
    private static final SecureRandom random = new SecureRandom();

    public static String generateCode() {
        int number = 100000 + random.nextInt(900000); // Génère un nombre entre 100000 et 999999
        return String.valueOf(number);
    }
}
