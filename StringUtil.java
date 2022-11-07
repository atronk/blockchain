package blockchain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class StringUtil {

    private static final String[] words = new String[]{
            "I",
            "you",
            "me",
            "your",
            "how",
            "when",
            "pest",
            "mixture",
            "biography",
            "meal",
            "maid",
            "resort",
            "sequence",
            "wardrobe",
            "replacement",
            "concede",
            "bill",
            "countryside",
            "needle",
            "collapse",
            "shave",
            "amuse",
            "hurl",
            "temple",
            "effective",
            "cultural"
    };

    /* Applies Sha256 to a string and returns a hash. */
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            /* Applies sha256 to our input */
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte elem : hash) {
                String hex = Integer.toHexString(0xff & elem);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String hashValues(Object... objects) {
        return StringUtil.applySha256(
                Arrays.stream(objects)
                        .map(String::valueOf)
                        .reduce("", (s1, s2) -> s1 + s2));
    }

    public static String capitalize(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    public static String randomCapString() {
        Random r = new Random();
        StringBuilder sbld = new StringBuilder();
        for (int i = 0; i < r.nextInt(2, 8); i++) {
            sbld.append((char) r.nextInt('a', 'a' + 26));
        }
        return capitalize(sbld.toString());
    }

    public static String getRandomText() {
        Random r = ThreadLocalRandom.current();
        StringBuilder sbd = new StringBuilder();
        int length = r.nextInt(10);
        for (int i = 0; i < length + 1; i++) {
            int index = r.nextInt(0, words.length);
            sbd.append(words[index]).append(" ");
        }
        sbd.append(".");
        return sbd.toString();
    }
}
