package cl.cooperativa.presidenciales2018;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by innova6 on 14-07-2017.
 */

public class ReporteroFragment extends Fragment implements
        View.OnClickListener,EasyPermissions.PermissionCallbacks{


    public ReporteroFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        showToolbar(getResources().getString(R.string.tab_reporteros),false,view);
      //  RecyclerView pictureRecycler=(RecyclerView) view.findViewById(R.id.pictureRecycler);

      //  new Downloader(this.getActivity(),urlAddress,pictureRecycler,true).execute();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
       // StorageActivity storageactivity = new StorageActivity();
        //storageactivity.launchCamera();

        lanzaStorage();


       return view;
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
    private void lanzaStorage(){

        Intent i = new Intent(this.getActivity(),StorageActivity.class);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {}

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {}

    @Override
    public void onClick(View v) {

    }
}
