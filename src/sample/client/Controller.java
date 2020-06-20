package sample.client;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import sample.server.DatabaseController;
import sample.shared.Message;
import sample.shared.PublicKeys;
import sample.shared.RSA;
import sample.shared.TEA;

import sample.shared.ServerConnectionEvents;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Controller extends ServerConnectionEvents {

    private RSA rsa;

    private TEA tea;

    private Client client;

    private PublicKeys serverPublicKey;

    private String TEAkey;

    @FXML
    TextArea message_box;

    @FXML
    TextArea message_list;

    @FXML
    TextArea public_key;

    @FXML
    TextArea public_exponent;



    public Controller() {

        rsa = new RSA(2048);
        TEAkey = "And is there honey still for tea?";
        tea = new TEA(TEAkey.getBytes());
        client = new Client("127.0.0.1", 8686, this);

    }

    public void initialize() {
        public_key.setText(rsa.getModulus().toString());
        public_key.setWrapText(true);
        public_exponent.setText(rsa.getPublicKey().toString());
    }

    public void connectServer() {

        client.setPublicKeys(getPublicKeys(rsa));

        client.start();
    }

    public void sendMessage() throws IOException, NoSuchAlgorithmException {
        //Send the symmetric key in order for the server to decrypt the image
        Message message = encrypt(rsa, TEAkey, serverPublicKey);

        //encrypt with tea
        byte[] cipherImage = encryptWithTea(tea,message_box);
        if (cipherImage == null) {
            JOptionPane.showMessageDialog(null, "Invalid Image Path");
            return;
        }

        //sign the image and save it in message
        message.setImageDigitalSignature(signImage(rsa, message_box.getText()));
        //send the encrypted image
        message.setCipherImage(cipherImage);
        message.setmUsername(DatabaseController.getInstance().getUsername());
        client.sendMessage(message);


    }

    void onMessageArrived(Message message) throws SQLException, NoSuchAlgorithmException {

        messageArrived(message, rsa, serverPublicKey, message_list);

    }

    void onStringMessageArrived(String message) throws SQLException, NoSuchAlgorithmException {

        message_list.appendText(message + "\n");

    }

    @Override
    public void onPublicKeyArrived(PublicKeys serverPublicKey) {
        this.serverPublicKey = serverPublicKey;
    }
}



