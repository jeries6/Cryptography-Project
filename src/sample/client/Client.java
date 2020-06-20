package sample.client;

import sample.shared.Message;
import sample.shared.PublicKeys;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class Client extends Thread {

    private PublicKeys publicKeys;

    private Controller clientController;

    private ObjectOutputStream out;
    private String address;
    private int port;

    // constructor to put ip address and port
    Client(String address, int port, Controller controller) {
        this.clientController = controller;
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        // establish a connection
        try {
            // initialize socket and input output streams
            Socket socket = new Socket(address, port);
            System.out.println("Connected");

            // takes input from terminal
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            // sends output to the socket
            out = new ObjectOutputStream(socket.getOutputStream());

            out.writeObject(this.publicKeys);

            Object object;

            while ((object = input.readObject()) != null) {

                if (object.getClass() == PublicKeys.class) {

                    this.clientController.onPublicKeyArrived((PublicKeys) object);

                } else if (object.getClass() == Message.class) {

                    clientController.onMessageArrived((Message) object);

                }else if(object.getClass() == String.class) {

                    clientController.onStringMessageArrived((String) object);

                }
            }

            System.out.println("closing connection");

            input.close();

            out.close();

            socket.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    void sendMessage(Message message) {

        try {

            out.writeObject(message);

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    void sendImage(byte[] image) {

        try {

            out.writeObject(image);

        } catch (IOException e) {

            e.printStackTrace();

        }
    }


    void setPublicKeys(PublicKeys publicKeys) {
        this.publicKeys = publicKeys;
    }

}