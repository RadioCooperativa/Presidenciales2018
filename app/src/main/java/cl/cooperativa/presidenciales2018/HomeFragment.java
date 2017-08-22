package cl.cooperativa.presidenciales2018;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by innova6 on 13-07-2017.
 */

public class HomeFragment extends Fragment {
    final static String urlAddress="https://m.cooperativa.cl/noticias/site/tax/port/all/rss_3_156__1.xml";


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        showToolbar(getResources().getString(R.string.tab_portada),false,view);
        RecyclerView pictureRecycler=(RecyclerView) view.findViewById(R.id.pictureRecycler);

      new Downloader(this.getActivity(),urlAddress,pictureRecycler,true).execute();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        pictureRecycler.setLayoutManager(linearLayoutManager);

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

}