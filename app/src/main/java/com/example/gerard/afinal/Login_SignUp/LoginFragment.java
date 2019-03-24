package com.example.gerard.afinal.Login_SignUp;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.view.ViewGroup;
import android.app.ProgressDialog;

import com.example.gerard.afinal.Account.ProfileFragment;
import com.example.gerard.afinal.EventFragment;
import com.example.gerard.afinal.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.support.v7.app.AppCompatActivity;

import java.util.Arrays;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.KEYGUARD_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class LoginFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private EventFragment event = new EventFragment();
    private SignUpFragment signUpFragment;
    private NormalUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRefUsers;
    private SignInButton mGoogleBtn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mCallbackManager;
    private OrganizationSignUp organizationSignUp;
    private boolean mIsResolving = false;
    private static int RC_SIGN_IN = 1;
    private LoginButton facebookloginButton;
    private GoogleApiClient mGoogleApiClient;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    boolean value;

    @BindView(R.id.link_signup) TextView _signupLink;
    @BindView(R.id.link_signuporganization) TextView _orgsignupLink;
    public LoginFragment(){

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView scrl = new ScrollView(getContext());
        signUpFragment = new SignUpFragment();
        scrl.setId(R.id.sign_in);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.sign_in_screen, container, false);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
             if(firebaseAuth.getCurrentUser()!=null){
                 getActivity().getSupportFragmentManager().beginTransaction()
                         .replace(R.id.main_fragment, event, "Home")
                         .addToBackStack(null)
                         .commit();
             }
            }
        };


        ButterKnife.bind(this,view);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Start the Signup activity
                signUpFragment = new SignUpFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, signUpFragment, "SignUp")
                        .addToBackStack(null)
                        .commit();
            }
        });

        _orgsignupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Start the Signup activity
                organizationSignUp = new OrganizationSignUp();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, organizationSignUp, "SignUp")
                        .addToBackStack(null)
                        .commit();
            }
        });
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            updateUI();
        }

    }

    private void updateUI() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment, event, "Home")
                .addToBackStack(null)
                .commit();
    }

    public void login() {
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;

        }
     mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
         @Override
         public void onComplete(@NonNull Task<AuthResult> task) {
             if (!task.isSuccessful()) {
                 Toast.makeText(getContext(), "Authentication failed.",Toast.LENGTH_SHORT).show();
                 return;
             }
             //else Authentication Successful
             Toast.makeText(getContext(), "Authentication Successful", Toast.LENGTH_SHORT).show();
             getActivity().getSupportFragmentManager().beginTransaction()
                     .replace(R.id.main_fragment, event, "Home")
                     .addToBackStack(null)
                     .commit();
         }
     });


        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");

        progressDialog.show();







        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();

                        // onLoginFailed();
                        progressDialog.dismiss();



                    }
                }, 3000);
    }



    public void onBackPressed() {
        // disable going back to the MainActivity
        getActivity().moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        //getActivity().finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {

        final String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("You should write between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }



return valid;

    }
    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database=FirebaseDatabase.getInstance();
        myRefUsers=database.getReference("Users");
        facebookloginButton= view.findViewById(R.id.login_facebookbutton);
        facebookloginButton.setFragment(this);
        mCallbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        // Initialize Facebook Login button


        facebookloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email","public_profile"));

                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        Log.d("FACELOG", "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("FACELOG", "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("FACELOG", "facebook:onError", error);
                        // ...
                    }
                });
            }
        });



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            if(firebaseAuth.getCurrentUser()!=null){
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, event, "Home")
                        .addToBackStack(null)
                        .commit();
            }
            }
        };
        mGoogleBtn = (SignInButton) view.findViewById(R.id.googleBtn);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext()).enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
               Toast.makeText(getContext(),"Connection Failed!",Toast.LENGTH_SHORT).show();
            }
        })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        mGoogleApiClient.clearDefaultAccountAndReconnect();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
        else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(),"Authentication Failed",Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });

    }

    private void handleFacebookAccessToken(AccessToken token) {

        AccessToken.getCurrentAccessToken().getPermissions();
        Log.d("FACELOG", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FACELOG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FACELOG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onClick(View v) {

    }
}