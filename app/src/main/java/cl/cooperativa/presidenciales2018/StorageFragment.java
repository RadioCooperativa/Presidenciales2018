package cl.cooperativa.presidenciales2018;

import android.app.Activity;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
 * Created by innova6 on 17-08-2017.
 */

public class StorageFragment extends Fragment implements
        View.OnClickListener,EasyPermissions.PermissionCallbacks {


    private static final String TAG = "StorageFragment";
    private static final int RC_TAKE_PICTURE = 101;
    private static final int RC_STORAGE_PERMS = 102;

    private static final String KEY_FILE_URI = "key_file_uri";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";
    private static final int CHOOSER_IMAGES = 1;

    private BroadcastReceiver mBroadcastReceiver;
    private ProgressDialog mProgressDialog=null;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference databaseReference;

    private Uri mDownloadUrl = null;
    private Uri mFileUri = null;
    public String mFileStorageUrl;
    public Long timestamp = System.currentTimeMillis() / 1000;
    public String imageName = timestamp.toString();

    Button button=null;
    ImageView imageView=null;
    EditText nombreReportero,correoReportero,textoReportero=null;
    private String DataBase_NODE_REPORTERO="presidenciales2018";
    private String nombreReporteroBD,correoReporteroBD,descripcionReporteroBD;
    private String BASE_URL_FIREBASE="https://firebasestorage.googleapis.com";

    Context context;



    public StorageFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_storage, container, false);
        showToolbar(getResources().getString(R.string.tab_storage),false,view);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Storage Ref
        // [START get_storage_ref]
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // [END get_storage_ref]

        // Initialize Firebase Database
        //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);//esperar conexión a internet
        databaseReference= FirebaseDatabase.getInstance().getReference();



        // Restore instance state
        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(KEY_FILE_URI);
            mDownloadUrl = savedInstanceState.getParcelable(KEY_DOWNLOAD_URL);
        }

        if (user != null){
            String nombreReporteroObtenido = user.getDisplayName();
            String mailReporteroObtenido = user.getEmail();
           // String descripcionReportero= String.valueOf(textoReportero=(EditText)view.findViewById(R.id.textoReportero));

            nombreReportero =(EditText)view. findViewById(R.id.nombreReportero);
            nombreReportero.setText(nombreReporteroObtenido);
            nombreReporteroBD=  nombreReportero.getText().toString();

            correoReportero=(EditText)view. findViewById(R.id.mailReportero);
            correoReportero.setText(mailReporteroObtenido);
            correoReporteroBD=correoReportero.getText().toString();

            textoReportero=(EditText)view.findViewById(R.id.textoReportero);
            //  textoReportero.setText(descripcionReportero);
            descripcionReporteroBD=textoReportero.getText().toString();
        }

        button=(Button)view. findViewById(R.id.btnUploadReportero);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                uploadFromUri(mFileUri);
                // creaReportero(mDownloadUrl);
            }
        });

        imageView=(ImageView)view. findViewById(R.id.imagenReportero);
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // updateUI(mAuth.getCurrentUser());

        // Register receiver for uploads and downloads
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        manager.registerReceiver(mBroadcastReceiver, MyDownloadService.getIntentFilter());
        manager.registerReceiver(mBroadcastReceiver, MyUploadService.getIntentFilter());
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unregister download receiver
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        out.putParcelable(KEY_FILE_URI, mFileUri);
        out.putParcelable(KEY_DOWNLOAD_URL, mDownloadUrl);
    }

    @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        if (requestCode ==CHOOSER_IMAGES) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i(TAG,"mFileUri es: "+mFileUri);
                // mFileUri = data.getData();

                if (mFileUri != null) {
                    //uploadFromUri(mFileUri);
                    imageView.setImageURI(mFileUri);
                } else {
                    Log.w(TAG, "File URI is null");
                }
            } else {
                Toast.makeText(context, "Taking picture failed.", Toast.LENGTH_SHORT).show();
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
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
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

                            // [END_EXCLUDE]
                        }
                    })
                    .addOnFailureListener(getActivity(), new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Upload failed
                            Log.w(TAG, "uploadFromUri:onFailure", exception);

                            mDownloadUrl = null;

                            // [START_EXCLUDE]
                            hideProgressDialog();
                            //Toast.makeText(StorageFragment.this, "Error: upload failed",
                                 //   Toast.LENGTH_SHORT).show();
                            Toast.makeText(context,"Error: a fallado la subida del archivo",Toast.LENGTH_SHORT);
                            //updateUI(mAuth.getCurrentUser());
                            // [END_EXCLUDE]
                        }
                    });

        }

    }

    @AfterPermissionGranted(RC_STORAGE_PERMS)
    public void launchCamera() {
        Log.d(TAG, "launchCamera");

        // Check that we have permission to read images from external storage.
        String perm = android.Manifest.permission.CAMERA;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !EasyPermissions.hasPermissions(getActivity(),perm)) {

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

    private void onUploadResultIntent(Intent intent) {
        // Got a new intent from MyUploadService with a success or failure
        mDownloadUrl = intent.getParcelableExtra(MyUploadService.EXTRA_DOWNLOAD_URL);
        mFileUri = intent.getParcelableExtra(MyUploadService.EXTRA_FILE_URI);
        Log.i(TAG,"onUploadResultIntent"+mFileUri);
        //  updateUI(mAuth.getCurrentUser());
    }

    private void showMessageDialog(String title, String message) {
        AlertDialog ad = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .create();
        ad.show();
    }

    private void showProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
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

    public void showToolbar(String tittle, boolean upButton, View view){
        /*Estamos en contexto de Fragment, es por eso que debe de llevar el código ((AppCompatActivity)getActivity())
        al llevar este código a un activity no debe llevar ese codigo
         */
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()). getSupportActionBar().setTitle(tittle);
        ((AppCompatActivity)getActivity()). getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {


    }
}
