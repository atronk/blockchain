package blockchain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;

public class Encryption {
    private KeyPairGenerator keyGen;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public Encryption(int keylength) {
        try {
            this.keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (Exception ignored) {
        }
        this.keyGen.initialize(keylength);
    }

    public static Encryption getGenerator() {
        return new Encryption(1024);
    }

    public static byte[] sign(String data, PrivateKey pk) throws Exception {
        Signature rsa = Signature.getInstance("SHA1withRSA");
        rsa.initSign(pk);
        rsa.update(data.getBytes());
        return rsa.sign();
    }

    public static boolean verifySignature(String data, byte[] signature, PublicKey pk) throws Exception {
        Signature sig = Signature.getInstance("SHA1withRSA");
        sig.initVerify(pk);
        sig.update(data.getBytes());

        return sig.verify(signature);
    }

    public void createKeys() {
        this.pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public void writeToFile(String path, byte[] key) throws IOException {
        File f = new File(path);
        f.getParentFile().mkdirs();

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(key);
        fos.flush();
        fos.close();
    }
}