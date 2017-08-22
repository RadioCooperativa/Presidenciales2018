package cl.cooperativa.presidenciales2018;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class AutenticationActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "AutenticationActivity";
    private static final int SIGN_IN_GOOGLE_CODE = 1;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient googleApiClient;
    private CallbackManager callbackManager;

    private Button btnCreateAccount;
    private Button btnSignIn;
    private SignInButton btnSignInGoogle;
    private LoginButton btnSignInFacebook;

    private EditText edtEmail;
    private EditText edtPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autentication);

        printKeyHash();

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        Log.w(TAG, "Callback  " + callbackManager.toString());

        btnCreateAccount    = (Button) findViewById(R.id.btnCreateAccount);
        btnSignIn           = (Button) findViewById(R.id.btnSignIn);
        btnSignInGoogle     = (SignInButton) findViewById(R.id.btnSignInGoogle);
        btnSignInFacebook   = (LoginButton) findViewById(R.id.btnSignInFacebook);

        edtEmail            = (EditText) findViewById(R.id.edtEmail);
        edtPassword         = (EditText) findViewById(R.id.edtPassword);

        initialize();

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(edtEmail.getText().toString(), edtPassword.getText().toString());
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(edtEmail.getText().toString(), edtPassword.getText().toString());
            }
        });

        btnSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_GOOGLE_CODE);
            }
        });

        btnSignInFacebook.setReadPermissions(Arrays.asList("email","public_profile"));
        btnSignInFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.w(TAG, "Facebook Login Success Token:  " + loginResult.getAccessToken().getToken());
                signInFacebookFirebase(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.w(TAG, "Facebook Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.w(TAG, "Facebook Error");
                error.printStackTrace();
            }
        });

    }

    private void initialize(){

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser !=  null){
                    Log.w(TAG, "onAuthStateChanged - signed_in" + firebaseUser.getUid());
                    Log.w(TAG, "onAuthStateChanged - signed_in" + firebaseUser.getEmail());
                }else {
                    Log.w(TAG, "onAuthStateChanged - signed_out");
                }
            }
        };

        //Inicialización de Google Account
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void  createAccount(String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AutenticationActivity.this, "Create Account Success", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AutenticationActivity.this, "Create Account Unsuccess", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void  signIn(String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AutenticationActivity.this, "Authentication Success", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(AutenticationActivity.this, ContainerActivity.class);
                    startActivity(i);
                    finish();
                }else {
                    Toast.makeText(AutenticationActivity.this, "Authentication Unsuccess", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signInGoogleFirebase(GoogleSignInResult googleSignInResult){
        if (googleSignInResult.isSuccess()){
            AuthCredential authCredential =
                    GoogleAuthProvider.getCredential(googleSignInResult.getSignInAccount().getIdToken(),null);
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(AutenticationActivity.this, "Google Authentication Success", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(AutenticationActivity.this, ContainerActivity.class);
                        startActivity(i);
                        finish();
                    }else {
                        Toast.makeText(AutenticationActivity.this, "Google Authentication Unsuccess", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(AutenticationActivity.this, "Google Sign In Unsuccess", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInFacebookFirebase(AccessToken accessToken){
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken.getToken());
        Log.w(TAG,"signInFacebookFirebase"+accessToken.getToken());
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.w(TAG,"signInFacebookFirebase Task: "+task);

                if (task.isSuccessful()){
                    Toast.makeText(AutenticationActivity.this, "Facebook Authentication Success", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(AutenticationActivity.this, ContainerActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(AutenticationActivity.this, "Facebook Authentication Unsuccess", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_GOOGLE_CODE){
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            signInGoogleFirebase(googleSignInResult);
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void printKeyHash() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("cl.cooperativa.presidenciales2018", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:", e.toString());
        }
    }
}

