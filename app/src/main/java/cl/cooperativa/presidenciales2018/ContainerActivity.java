package cl.cooperativa.presidenciales2018;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.IOException;


public class ContainerActivity extends AppCompatActivity implements
        View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "ContainerActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleApiClient googleApiClient;

    private TextView tvUserDetail;
    private Button btnSignOut;
    private ImageView imvPhoto;

    public String urlMediaPlayer ="http://unlimited3-cl.dps.live/cooperativafm/aac/icecast.audio";
    public MediaPlayer mediaPlayer = new MediaPlayer();
    //ProgressBar progressBar;
    public cargaMedia cmedia;
    FloatingActionButton floatingActionButton;
    FloatingActionButton floatingActionButtonPause;
    FloatingActionButton floatingActionButtonPlay;

    //ProgressDialog loading;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        loading = new ProgressDialog(ContainerActivity.this);

        initialize();
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottonbar);
        bottomBar.setDefaultTab(R.id.portada);

       // progressBar=(ProgressBar) findViewById(R.id.fabProgressBar);

        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null).commit();

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButtonPause=(FloatingActionButton) findViewById(R.id.fabPause);
        floatingActionButtonPlay=(FloatingActionButton) findViewById(R.id.fabPlay);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                floatingActionButton.setVisibility(View.INVISIBLE);
                cmedia=new cargaMedia();
                cmedia.execute();

            }

        });

        floatingActionButtonPause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                try {

                    floatingActionButtonPlay.setVisibility(View.VISIBLE);
                    floatingActionButtonPause.setVisibility(View.INVISIBLE);
                    pausaMedia();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        floatingActionButtonPlay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                try {
                    floatingActionButton.setVisibility(View.INVISIBLE);
                    floatingActionButtonPlay.setVisibility(View.INVISIBLE);
                    floatingActionButtonPause.setVisibility(View.VISIBLE);
                    playMedia();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId){
                    case R.id.portada:
                        HomeFragment homeFragment = new HomeFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null).commit();
                        break;
                    case R.id.perfil:
                        PerfilFragment perfilFragment = new  PerfilFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,perfilFragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null).commit();
                        break;
                    case R.id.reportero:

                        StorageFragment storageFragment = new  StorageFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,storageFragment )
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null).commit();
                      //  reporteroFragment.launchCamera();
                        break;

                    case R.id.propuestas:
                        PropuestasFragment propuestasFragment = new PropuestasFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,propuestasFragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null).commit();
                        break;

                   case R.id.resultados:
                        ResultadosFragment resultadosFragment = new ResultadosFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,resultadosFragment)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null).commit();
                        break;
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_container, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }


    public class cargaMedia extends AsyncTask<Void,Integer,Boolean> {

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
        @Override
        protected void onPreExecute() {
           // loading=new ProgressDialog(ContainerActivity.this);
            //progressBar.setVisibility(View.VISIBLE);
           // loading.show(ContainerActivity.this,"Cargando Medios", "Un momento por favor...",false,false);
           loading= ProgressDialog.show(ContainerActivity.this,"Cargando Medios", "Un momento por favor...",false,false);
            // final ProgressDialog pd = ProgressDialog.show(ContainerActivity.this, "", "Loading...", false, true);

            //System.out.println("ContainerActivity, progressbar Visible");
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
          //  progressBar.setVisibility(View.INVISIBLE);
           // loading = new ProgressDialog(ContainerActivity.this);
            loading.dismiss();

           // System.out.println("ContainerActivity, progressbar INVisible");
            floatingActionButtonPause.setVisibility(View.VISIBLE);

        }
        @Override
        protected Boolean doInBackground(Void... params) {

            try{
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(urlMediaPlayer);
                mediaPlayer.prepare();
                mediaPlayer.setVolume(1,1);
                mediaPlayer.start();
            }catch (IOException e)
            {
                e.printStackTrace();
            }
            return true;
        }
    }


    public void playMedia() throws IOException{

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {

            //mPlayer.pause();
            mediaPlayer.setVolume(1,1);
        }
    }
    public void pausaMedia() throws IOException{

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {

            //mPlayer.pause();
            mediaPlayer.setVolume(0,0);
        }
    }
    @Override
    protected void onPause()
    {
        super.onPause();
    }
    protected void onResume() {

        super.onResume();
    }
    protected void onDestroy() {
        super.onDestroy();

    }


    private void initialize(){

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser !=  null){
                    //tvUserDetail.setText("IDUser: " + firebaseUser.getUid() + " Email: " + firebaseUser.getEmail());
                    //Picasso.with(ContainerActivity.this).load(firebaseUser.getPhotoUrl()).into(imvPhoto);
                }else {
                    Log.w(TAG, "onAuthStateChanged - signed_out");
                }
            }
        };

      /*  //Inicialización de Google Account
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();*/
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
