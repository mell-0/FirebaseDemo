package aydin.firebasedemo;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class WelcomeScreenController
{
    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private Button sign_in;

    @FXML
    private Button register;


    void initialize()
    {
        AccessDataView accessDataViewModel = new AccessDataView();
        usernameTextField.textProperty().bindBidirectional(accessDataViewModel.personNameProperty());
        register.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());

        System.out.println("initialize");
        System.out.println(usernameTextField.getText());
    }

    @FXML
    void sign_inClicked(ActionEvent event)
    {

    }

    @FXML
    void registerClicked(ActionEvent event)
    {
        System.out.println("reg clicked");
        registerUser();
    }

    /*
        - phone email and phone number needs to be unique for register to work
        - email is format checked to see if there's an @ & .com
        - phone number is checked to see if length is correct

     */
    public boolean registerUser() {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(usernameTextField.getText())
                .setEmailVerified(false)
                .setPassword(passwordTextField.getText())
                .setPhoneNumber("+15234567890")
                .setDisplayName("Jon Doe")
                .setDisabled(false);

        UserRecord userRecord;
        try {
            userRecord = DemoApp.fauth.createUser(request);
            System.out.println("Successfully created new user with Firebase Uid: " + userRecord.getUid()
                    + " check Firebase > Authentication > Users tab");
            return true;

        } catch (FirebaseAuthException ex) {
            // Logger.getLogger(FirestoreContext.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error creating a new user in the firebase");
            return false;
        }
    }

}
