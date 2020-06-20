package sample.shared;

import javafx.scene.control.TextArea;
import sample.server.DatabaseController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Formatter;

/**
 * This class has shared methods of clients and server
 */

public abstract class ServerConnectionEvents {


    protected PublicKeys getPublicKeys(RSA rsa) {

        PublicKeys publicKeys = new PublicKeys();

        publicKeys.setPublicExponent(rsa.getPublicKey());

        publicKeys.setPublicModulo(rsa.getModulus());

        return publicKeys;
    }

    /**
     * accepts
     * : RSA class that has implementation of RSA
     * : TextArea that contains a message to be sent
     * : PublicKeys a receiver public keys
     * : returns encrypted Message
     * */
    protected Message encrypt(RSA rsa, String messageToEncrypt, PublicKeys publicKeys) {

        String encrypted = rsa.encrypt(messageToEncrypt, publicKeys.getPublicExponent(), publicKeys.getPublicModulo());

        String signature = rsa.signature(messageToEncrypt);

        System.out.println(signature);

        Message message = new Message();

        message.setDigitalSignature(signature);

        message.setEncryptedMessage(encrypted);

        return message;
    }
    /**
     * accepts
     * : RSA class that has implementation of RSA
     * : TextArea that contains a message to be sent
     * : PublicKeys a receiver public keys
     * : returns encrypted Message
     * */
    protected Message encrypt(RSA rsa, TextArea messageToEncrypt, PublicKeys publicKeys) {

        String messageEncrypted = messageToEncrypt.getText();

        String encrypted = rsa.encrypt(messageEncrypted, publicKeys.getPublicExponent(), publicKeys.getPublicModulo());

        String signature = rsa.signature(messageEncrypted);

        System.out.println(signature);

        Message message = new Message();

        message.setDigitalSignature(signature);

        message.setEncryptedMessage(encrypted);

        return message;
    }
    /**
     * method that checks if digital signature is valid and displays the arrived on GUI
     * accepts Message a serialized class contains both signature and hashed message,
     * RSA class to verify signature,
     * PublicKeys a key to verify digital sign
     * TextArea to append message on
     * */
    protected void messageArrived(Message message, RSA rsa, PublicKeys serverPublicKey, TextArea message_list) throws NoSuchAlgorithmException, SQLException {

        BigInteger decryptedMsg = rsa.decrypt(message.getEncryptedMessage());
        TEA tea = new TEA(decryptedMsg.toByteArray());
        byte[] decryptedImage =  tea.decrypt(message.getCipherImage());

        DatabaseController dbController=DatabaseController.getInstance();

        //Hash the encrypted image
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        String resultOfHash = byteArray2Hex(md.digest(decryptedImage));

        boolean imgVerify = false;
        if(dbController.verifyImage(resultOfHash, message.getmUsername())){
            System.out.println("Passed: Image verified In Database!");
            imgVerify = true;
        }
        else {
            System.out.println("Failed: Couldn't verify your image");
        }


        byte[] bytes = decryptedMsg.toByteArray();

        String msg = new String(bytes);

        System.out.println(msg);

        //verify signature of the image
        byte[] imgBytes = resultOfHash.getBytes();
        BigInteger messageBigInt = new BigInteger(imgBytes);
        boolean imgVerified = rsa.verifySignature(message.getImageDigitalSignature(),
                serverPublicKey.getPublicExponent(), serverPublicKey.getPublicModulo(), messageBigInt);

        //verify signature of the key
        boolean verified = rsa.verifySignature(message.getDigitalSignature(),
                serverPublicKey.getPublicExponent(), serverPublicKey.getPublicModulo(), decryptedMsg);


        if (verified && imgVerified && imgVerify) {

            //message_list.appendText(msg + "\n");
            message_list.appendText("Verified!!!" + "\n");
            Message.hasAccess = true;

        } else {

            message_list.appendText("Unverified message \n");
            Message.hasAccess = false;

        }
    }



    /**
     * this method notify subscriber that key is arrived
     * */
    public abstract void onPublicKeyArrived(PublicKeys publicKeys);


    /**
     * Encrypt the picture using TEA
     * @param tea
     * @param message_box
     * @return
     * @throws IOException
     */
    protected byte[] encryptWithTea(TEA tea, TextArea message_box) throws IOException {

        byte[] imageInBytes;

            File file = new File(message_box.getText());
            if (!file.exists())
                return null;
            FileInputStream imageStream = new FileInputStream(message_box.getText());
            imageInBytes = new byte[imageStream.available()];
            imageStream.read(imageInBytes);



        return tea.encrypt(imageInBytes);
}


    private String byteArray2Hex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public String signImage(RSA rsa, String imagePath) throws IOException, NoSuchAlgorithmException {

        File file = new File(imagePath);
        FileInputStream imageStream = new FileInputStream(imagePath);
        byte[] imageInBytes = new byte[imageStream.available()];
        imageStream.read(imageInBytes);

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        String resultOfHash = byteArray2Hex(md.digest(imageInBytes));

       // String stringOfImage = new String(imageInBytes);
        return rsa.signature(resultOfHash);

    }



}