package cl.cooperativa.presidenciales2018;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by innova6 on 08-08-2017.
 */

public class StorageActivity extends AppCompatActivity implements
        View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "StorageActivity";

    private static final int RC_TAKE_PICTURE = 101;
    private static final int RC_STORAGE_PERMS = 102;

    private static final String KEY_FILE_URI = "key_file_uri";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";
    private static final int CHOOSER_IMAGES = 1;

    private BroadcastReceiver mBroadcastReceiver;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseReference;

    private Uri mDownloadUrl = null;
    private Uri mFileUri = null;
    public String mFileStorageUrl;
    public Long timestamp = System.currentTimeMillis() / 1000;
    public String imageName = timestamp.toString();

    Button button;
    ImageView imageView;
    EditText nombreReportero,correoReportero,textoReportero;
    private String DataBase_NODE_REPORTERO="presidenciales2018";
    private String nombreReporteroBD,correoReporteroBD,descripcionReporteroBD;
    private String BASE_URL_FIREBASE="https://firebasestorage.googleapis.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitystorage);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Storage Ref
        // [START get_storage_ref]
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // [END get_storage_ref]

       // Initialize Firebase Database
      //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);//esperar conexión a internet
        databaseReference=FirebaseDatabase.getInstance().getReference();



        // Restore instance state
        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(KEY_FILE_URI);
            mDownloadUrl = savedInstanceState.getParcelable(KEY_DOWNLOAD_URL);
        }

        onNewIntent(getIntent());

        if (user != null){
            String nombreReporteroObtenido = user.getDisplayName();
            String mailReporteroObtenido = user.getEmail();
            String descripcionReportero= String.valueOf(textoReportero=(EditText) findViewById(R.id.textoReportero));

            nombreReportero =(EditText) findViewById(R.id.nombreReportero);
            nombreReportero.setText(nombreReporteroObtenido);
            nombreReporteroBD=  nombreReportero.getText().toString();

            correoReportero=(EditText) findViewById(R.id.mailReportero);
            correoReportero.setText(mailReporteroObtenido);
            correoReporteroBD=correoReportero.getText().toString();

            textoReportero=(EditText)findViewById(R.id.textoReportero);
          //  textoReportero.setText(descripcionReportero);
            descripcionReporteroBD=textoReportero.getText().toString();
        }



        button=(Button) findViewById(R.id.btnUploadReportero);
        button.setOnClickListener(new View.OnClickListener() {

        @Override
            public void onClick(View v) {
            uploadFromUri(mFileUri);
           // creaReportero(mDownloadUrl);
                }
        });

        imageView=(ImageView) findViewById(R.id.imagenReportero);
        imageView.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
              /*  Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"Selecciona una Imagen"),CHOOSER_IMAGES);*/

               launchCamera();
            }
        });


        // Local broadcast receiver
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive:" + intent);
                hideProgressDialog();

                switch (intent.getAction()) {
                    case MyDownloadService.DOWNLOAD_COMPLETED:

                        // Get number of bytes downloaded
                        long numBytes = intent.getLongExtra(MyDownloadService.EXTRA_BYTES_DOWNLOADED, 0);

                        // Alert success
                        showMessageDialog(getString(R.string.success), String.format(Locale.getDefault(),
                                "%d bytes downloaded from %s",
                                numBytes,
                                intent.getStringExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH)));
                        break;
                    case MyDownloadService.DOWNLOAD_ERROR:
                        // Alert failure
                        showMessageDialog("Error", String.format(Locale.getDefault(),
                                "Failed to download from %s",
                                intent.getStringExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH)));
                        break;
                    case MyUploadService.UPLOAD_COMPLETED:
                    case MyUploadService.UPLOAD_ERROR:
                        onUploadResultIntent(intent);
                        break;
                }
            }
        };

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Check if this Activity was launched by clicking on an upload notification
        if (intent.hasExtra(MyUploadService.EXTRA_DOWNLOAD_URL)) {
            onUploadResultIntent(intent);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
       // updateUI(mAuth.getCurrentUser());

        // Register receiver for uploads and downloads
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mBroadcastReceiver, MyDownloadService.getIntentFilter());
        manager.registerReceiver(mBroadcastReceiver, MyUploadService.getIntentFilter());
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unregister download receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        out.putParcelable(KEY_FILE_URI, mFileUri);
        out.putParcelable(KEY_DOWNLOAD_URL, mDownloadUrl);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        if (requestCode ==CHOOSER_IMAGES) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG,"mFileUri es: "+mFileUri);
              // mFileUri = data.getData();

                if (mFileUri != null) {
                   //uploadFromUri(mFileUri);
                    imageView.setImageURI(mFileUri);
                } else {
                    Log.w(TAG, "File URI is null");
                }
           } else {
                Toast.makeText(this, "Taking picture failed.", Toast.LENGTH_SHORT).show();
           }
        }
    }

    private void uploadFromUri(Uri fileUri) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        if (user != null){
            // [START get_child_ref]
            // Get a reference to store file at photos/<FILENAME>.jpg
            final StorageReference photoRef = mStorageRef.child("fotosElecciones2017")
                   .child(fileUri.getLastPathSegment());

            // [END get_child_ref]

        // Upload file to Firebase Storage
        // [START_EXCLUDE]
        showProgressDialog(getString(R.string.progress_downloading));
        // [END_EXCLUDE]
        Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());
        photoRef.putFile(fileUri)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Upload succeeded

                        // Get the public download URL
                        mDownloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                        mFileStorageUrl=mDownloadUrl.getPath();
                        Log.d(TAG, "uploadFromUri:onSuccess mFileStorageUrl"+mDownloadUrl);
                        creaReportero(mDownloadUrl);

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        finish();

                        //   updateUI(mAuth.getCurrentUser());
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w(TAG, "uploadFromUri:onFailure", exception);

                        mDownloadUrl = null;

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        Toast.makeText(StorageActivity.this, "Error: upload failed",
                                Toast.LENGTH_SHORT).show();
                        //updateUI(mAuth.getCurrentUser());
                        // [END_EXCLUDE]
                    }
                });

        }

    }

    private void beginDownload() {
        // Get path
        String path = "photos/" + mFileUri.getLastPathSegment();

        // Kick off MyDownloadService to download the file
        Intent intent = new Intent(this, MyDownloadService.class)
                .putExtra(MyDownloadService.EXTRA_DOWNLOAD_PATH, path)
                .setAction(MyDownloadService.ACTION_DOWNLOAD);
        startService(intent);

        // Show loading spinner
        showProgressDialog(getString(R.string.progress_downloading));
    }
    @AfterPermissionGranted(RC_STORAGE_PERMS)
    public void launchCamera() {
        Log.d(TAG, "launchCamera");

        // Check that we have permission to read images from external storage.
       String perm = Manifest.permission.CAMERA;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !EasyPermissions.hasPermissions(this, perm)) {

            System.out.println("Version Code > M");
           EasyPermissions.requestPermissions(this, getString(R.string.rationale_storage),RC_STORAGE_PERMS, perm);

            return;
        }

       // Create intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Choose file storage location

        File file = new File(Environment.getExternalStorageDirectory(), (imageName) + ".jpg");
        mFileUri = Uri.fromFile(file);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);

        // Launch intent
          startActivityForResult(takePictureIntent, CHOOSER_IMAGES);

    }

    public void creaReportero(Uri mFileUri){
       System.out.println("creaReportero: "+descripcionReporteroBD);
        ElectorReportero electorReportero = new ElectorReportero(imageName,nombreReporteroBD,
               correoReporteroBD ,mFileUri.toString(),descripcionReporteroBD);
        databaseReference.child(DataBase_NODE_REPORTERO).child(electorReportero.getIdtsReportero()).setValue(electorReportero);
    }

    private void signInAnonymously() {
        // Sign in anonymously. Authentication is required to read or write from Firebase Storage.
        showProgressDialog(getString(R.string.progress_auth));
        mAuth.signInAnonymously()
                .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "signInAnonymously:SUCCESS");
                        hideProgressDialog();
                       // updateUI(authResult.getUser());
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "signInAnonymously:FAILURE", exception);
                        hideProgressDialog();
                       // updateUI(null);
                    }
                });
    }

    private void onUploadResultIntent(Intent intent) {
        // Got a new intent from MyUploadService with a success or failure
        mDownloadUrl = intent.getParcelableExtra(MyUploadService.EXTRA_DOWNLOAD_URL);
        mFileUri = intent.getParcelableExtra(MyUploadService.EXTRA_FILE_URI);
        Log.i(TAG,"onUploadResultIntent"+mFileUri);
      //  updateUI(mAuth.getCurrentUser());
    }

   /* private void updateUI(FirebaseUser user) {
        // Signed in or Signed out
        if (user != null) {
            findViewById(R.id.layout_signin).setVisibility(View.GONE);
            findViewById(R.id.layout_storage).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.layout_signin).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_storage).setVisibility(View.GONE);
        }

        // Download URL and Download button
        if (mDownloadUrl != null) {
            ((TextView) findViewById(R.id.picture_download_uri))
                    .setText(mDownloadUrl.toString());
            findViewById(R.id.layout_download).setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.picture_download_uri))
                    .setText(null);
            findViewById(R.id.layout_download).setVisibility(View.GONE);
        }
    }*/

    private void showMessageDialog(String title, String message) {
        AlertDialog ad = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .create();
        ad.show();
    }

    private void showProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/
   @Override
   public void onClick(View v) {

   }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }
}
