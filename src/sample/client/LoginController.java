package sample.client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;


import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.server.DatabaseController;

import javax.swing.*;
import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private Button signInBtn;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Text actiontarget;

    public LoginController() {
    }

    @FXML
    void handleSubmitButtonAction(ActionEvent event) throws SQLException, IOException {

        DatabaseController databaseController = DatabaseController.getInstance();

        if(databaseController.verifyUser(usernameField.getText(),passwordField.getText())){
            databaseController.setUsername(usernameField.getText());
            databaseController.setPassword(passwordField.getText());

            Parent tableViewParent = FXMLLoader.load(getClass().getResource("client.fxml"));
            Scene tableViewScene = new Scene(tableViewParent);

            //This line gets the Stage information
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

            window.setScene(tableViewScene);
            window.show();
        }
        else
            JOptionPane.showMessageDialog(null, "Oops, You have entered an invalid username or password");



    }

}
