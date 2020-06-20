package sample.shared;

import java.io.Serializable;

public class Message implements Serializable {


    private String mUsername;

    private String encryptedMessage;

    private String digitalSignature;

    private String imageDigitalSignature;

    private byte[] cipherImage;

    public static boolean hasAccess = false;


    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }


    String getEncryptedMessage() {
        return encryptedMessage;
    }

    public String getDigitalSignature() {
        return digitalSignature;
    }

    void setEncryptedMessage(String encryptedMessage) {
        this.encryptedMessage = encryptedMessage;
    }

    void setDigitalSignature(String digitalSignature) {
        this.digitalSignature = digitalSignature;
    }

    @Override
    public String toString() {
        System.out.println( "********** message **********");
        System.out.println("enc msg " + getEncryptedMessage());
        System.out.println("dig sign " + getDigitalSignature());
        return super.toString();
    }

    public String getImageDigitalSignature() {
        return imageDigitalSignature;
    }

    public void setImageDigitalSignature(String imageDigitalSignature) {
        this.imageDigitalSignature = imageDigitalSignature;
    }


    public byte[] getCipherImage() {
        return cipherImage;
    }

    public void setCipherImage(byte[] cipherImage) {
        this.cipherImage = cipherImage;
    }
}
