package aydin.firebasedemo;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.remoteconfig.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class WelcomeScreenController
{
    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField passwordTextField;

//    @FXML
//    private TextField phoneNumTextField;

    @FXML
    private Button sign_in;

    @FXML
    private Button register;


    void initialize()
    {
        AccessDataView accessDataViewModel = new AccessDataView();
        usernameTextField.textProperty().bindBidirectional(accessDataViewModel.personNameProperty());
        register.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());
    }

    @FXML
    void sign_inClicked(ActionEvent event) throws IOException {
        System.out.println("Sign in clicked");

        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        // if signIn returns true if matching username & password was found & goes to the primary screen
        if (signIn(username, password))
        {
            System.out.println(username + " is in database");
            try {
                switchToPrimary();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else
            System.out.println(username + " is not in database or password wrong");
    }

    /*
        registerClicked method
        - When register button is clicked, method will call register user & adds the email, password & phone# into the
        authentication & if that's successful, registerUser method will return true and also add the email as the username
        and password into the database
     */
    @FXML
    void registerClicked(ActionEvent event)
    {
        System.out.println("register clicked");

        // only store user to database if it's valid or if it's not already there
        if (registerUser())
            addUser();
    }

    /*
        - phone email and phone number needs to be unique for register to work
        - email is format checked to see if there's an @ & .com
        - phone number is checked to see if length is correct
        - phone number needs to be in this format +12344567899 -> +1 123 123 1234
        - password needs to be at least 6 letters long

     */
    public boolean registerUser() {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(usernameTextField.getText())
                .setEmailVerified(false)
                .setPassword(passwordTextField.getText())
                //.setPhoneNumber(phoneNumTextField.getText())
                //.setDisplayName("Jon Doe")
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

    /*
        add user
     */
    public void addUser() {
        // creates the Users collection in the db
        DocumentReference docRef = DemoApp.fstore.collection("Users").document(UUID.randomUUID().toString());

        // creates the fields in the Users collection
        Map<String, Object> data = new HashMap<>();
        data.put("UserName", usernameTextField.getText());
        data.put("Password", passwordTextField.getText());

        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
    }

    /*
        signIn method
        - takes in username and password which is sent from the username and password text fields.
        It goes through the firestore database user's document and checks if there's a matching username &
        password. Returns true if a matching was found & false if not
        @param username - the email that was taken from the username textfield
        @param password - the password that was taken from the password textfield
     */
    public boolean signIn(String username, String password)
    {
        ApiFuture<QuerySnapshot> future = DemoApp.fstore.collection("Users").get();

        List<QueryDocumentSnapshot> documents;
        try
        {
            documents = future.get().getDocuments();
            if(documents.size()>0)
            {
                System.out.println("Checking if user is in database...");

                // user and pass are used to store the usernames and passwords in the firestore database
                String user = "", pass;
                boolean found = false;

                // going through the document list
                for (QueryDocumentSnapshot document : documents)
                {
                    System.out.println(document.getId() + " => " + document.getData().get("UserName")
                            + " " + document.getData().get("Password"));

                    user = document.getData().get("UserName").toString();
                    pass = document.getData().get("Password").toString();

                    // exits the method and returns true when matching username & password was found
                    if (username.equals(user) && password.equals(pass))
                        return true;
                }
            }
            else
            {
                System.out.println("No Users in the database");
            }

        } catch (InterruptedException | ExecutionException ex)
        {
            ex.printStackTrace();
        }
        return false;
    }

    /*
        switchToPrimary method
        goes to the next screen if sign in was successful
     */
    private void switchToPrimary() throws IOException {
        DemoApp.setRoot("primary");
    }

}
