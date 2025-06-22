package com.vishnu.voigoorder.ui.authentication.signInWithEmailPswd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vishnu.voigoorder.R;
import com.vishnu.voigoorder.callbacks.EmailVerification;
import com.vishnu.voigoorder.databinding.FragmentRegistrationBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterFragment extends Fragment {
    private static final String LOG_TAG = "RegisterFragment";
    FragmentRegistrationBinding binding;
    EditText emailET;
    EditText passwordET;
    EditText reenterPasswordET;
    Context context;
    Activity activity;
    Button registerBtn;
    TextView statusTV;
    private FirebaseAuth mAuth;
    Map<String, Object> userDataMap;
    Map<String, Object> userDataKeyMap;
    DocumentReference registeredUsersCredentialsRef;
    DocumentReference registeredUsersEmailRef;

    public RegisterFragment(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        registeredUsersCredentialsRef = db.collection("AuthenticationData").document("RegisteredUsersCredentials");
        registeredUsersEmailRef = db.collection("AuthenticationData").document("RegisteredUsersEmail");

        userDataKeyMap = new HashMap<>();
        userDataMap = new HashMap<>();

        emailET = binding.emailFieldRegisterFragmentEditTextText;
        passwordET = binding.passwordFieldRegisterFragmentEditTextText;
        reenterPasswordET = binding.reEnterPasswordFieldRegisterFragmentEditTextText;
        registerBtn = binding.registerRegisterFragmentButton;
        statusTV = binding.statusViewRegisterFragmentEditTextText;

        registerBtn.setOnClickListener(v -> {
            if (!emailET.getText().toString().isEmpty() && !passwordET.getText().toString().isEmpty()
                    && !reenterPasswordET.getText().toString().isEmpty()) {
                if (passwordET.getText().length() >= 6) {
                    if (reenterPasswordET.getText().toString().equals(passwordET.getText().toString())) {
                        statusTV.setText(R.string.Please_wait);
                        registerUserInFirestore(emailET.getText().toString());
                    } else {
                        statusTV.setText(R.string.password_not_match);
                    }
                } else {
                    statusTV.setText(R.string.length_should_be_6_or_more);
                }
            } else if (emailET.getText().toString().isEmpty() && passwordET.getText().toString().isEmpty()) {
                statusTV.setText(R.string.fields_can_t_be_empty);
            } else if (emailET.getText().toString().isEmpty()) {
                statusTV.setText(R.string.please_enter_the_email_id);
            } else if (passwordET.getText().toString().isEmpty()) {
                statusTV.setText(R.string.please_enter_the_password);
            } else if (reenterPasswordET.getText().toString().isEmpty()) {
                statusTV.setText(R.string.re_enter_password);
            }
        });

        return root;
    }


//    private void updateUserCredsToDB(String emailId, String pswd, String uid) {
//        userDataMap.put("email_id", emailId);
//        userDataMap.put("password", pswd);
//        userDataMap.put("userID", uid);
//
//        userDataKeyMap.put(emailId.replace('.','_'), userDataMap);
//
//        registeredUsersCredentialsRef.get().addOnSuccessListener(documentSnapshot -> {
//            if (documentSnapshot.exists()) {
//                // Update the map-type data
//                registeredUsersCredentialsRef.update(userDataKeyMap)
//                        .addOnSuccessListener(aVoid -> Log.d(LOG_TAG, "User data updated successfully"))
//                        .addOnFailureListener(e -> Log.e(LOG_TAG, "Error updating user data", e));
//            } else {
//                registeredUsersCredentialsRef.set(userDataKeyMap)
//                        .addOnSuccessListener(aVoid -> Log.d(LOG_TAG, "User data added successfully"))
//                        .addOnFailureListener(e -> Log.e(LOG_TAG, "Error adding user data", e));
//            }
//
//        });
//    }

    private void updateUserEmailToDB(String itemToAdd) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("email_addresses", FieldValue.arrayUnion(itemToAdd));

        registeredUsersEmailRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                registeredUsersEmailRef.update(updateData)
                        .addOnSuccessListener(aVoid -> Log.d(LOG_TAG, "User email updated to array successfully"))
                        .addOnFailureListener(e -> Log.e(LOG_TAG, "Error updating item to array", e));
            } else {
                registeredUsersEmailRef.set(updateData)
                        .addOnSuccessListener(aVoid -> Log.d(LOG_TAG, "User email added to array successfully"))
                        .addOnFailureListener(e -> Log.e(LOG_TAG, "Error adding item to array", e));
            }
        });
    }

    private void registerUserInFirestore(String emailToCheck) {
        registeredUsersEmailRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Object emailAddressesObj = documentSnapshot.get("email_addresses");
                        if (emailAddressesObj instanceof List<?> emailAddressesList) {
                            for (Object emailObj : emailAddressesList) {
                                if (emailObj instanceof String email) {
                                    if (email.equals(emailToCheck)) {
                                        statusTV.setText(R.string.email_address_has_already_taken);
                                        emailET.setText("");
                                        Log.d(LOG_TAG, "Email " + emailToCheck + " exists in db");
                                        return;
                                    }
                                }
                            }
                            Log.d(LOG_TAG, "Email " + emailToCheck + " does not exist in db");
                            addUser(emailET.getText().toString(), passwordET.getText().toString());
                        } else {
                            Log.e(LOG_TAG, "Email addresses is not a list");
                        }
                    } else {
                        Log.e(LOG_TAG, "Document 'RegisteredUsersEmail' does not exist");
                    }
                })
                .addOnFailureListener(e -> Log.e(LOG_TAG, "Error checking email in db", e));
    }

    private void sendEmailVerification(FirebaseUser user, EmailVerification callback) {
        user.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(LOG_TAG, "Email verification sent.");
                Toast.makeText(context, "Email verification sent", Toast.LENGTH_SHORT).show();
                statusTV.setText(R.string.verify_login_again);
                callback.onEmailVerificationSent(true);
            } else {
                Toast.makeText(context, "Failed to send email verification", Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "Failed to send email verification.", task.getException());
                callback.onEmailVerificationSent(false);
            }
        });
    }

    private boolean isGmailAppInstalled() {
        PackageManager pm = requireActivity().getPackageManager();
        Intent gmailIntent = new Intent(Intent.ACTION_MAIN);
        gmailIntent.addCategory(Intent.CATEGORY_APP_EMAIL);
        ResolveInfo resolveInfo = pm.resolveActivity(gmailIntent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo != null && resolveInfo.activityInfo.packageName.equals("com.google.android.gm");
    }

    private void showOpenGmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Account verification");
        builder.setMessage("Please verify your email address. Do you want to open Gmail?");
        builder.setCancelable(false);

        builder.setPositiveButton("Open Gmail", (dialog, which) -> {
            // Perform account-deletion action
            if (isGmailAppInstalled()) {
                // Open the Gmail app to the main screen
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Error opening Gmail", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Gmail app is not installed", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));
        builder.show();
    }

    private void addUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        statusTV.setText(R.string.registration_successful);
                        Log.d(LOG_TAG, "createUserWithEmail:success");

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            /* Send email verification */
                            sendEmailVerification(user, isSuccess -> showOpenGmailDialog());

                            /* Update user credentials and email in Firestore */
//                            updateUserCredsToDB(email, password, user.getUid());
                            updateUserEmailToDB(email);

                            emailET.setText("");
                            passwordET.setText("");
                            reenterPasswordET.setText("");

                            Toast.makeText(context, "Account created successfully.\n " +
                                    "Verify email and login again", Toast.LENGTH_LONG).show();

                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        statusTV.setText(R.string.authentication_fail);
                        Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show();
                        Log.w(LOG_TAG, "createUserWithEmail:failure", task.getException());
                    }
                });
    }

}