package cl.cooperativa.presidenciales2018;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by innova6 on 14-07-2017.
 */

public class ResultadosFragment extends Fragment {
    final static String urlAddress="https://m.cooperativa.cl/noticias/site/tax/port/all/rss_3_156_1471_1.xml";
    private List<Model> listResultados;
    Context context=getContext();

    //Creating Views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    public ResultadosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        showToolbar(getResources().getString(R.string.tab_resultados),false,view);
       recyclerView=(RecyclerView) view.findViewById(R.id.pictureRecycler);

      //  new Downloader(this.getActivity(),urlAddress,pictureRecycler,true).execute();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);

        //Initializing our superheroes list
        listResultados = new ArrayList<>();

        //Calling method to get data
        getData();
        return view;

    }
    //This method will get data from the web api
    private void getData(){
        //Showing a progress dialog
        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Loading Data", "Please wait...",false,false);

        //Creating a json array request
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Config.DATA_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Dismissing progress dialog
                       loading.dismiss();

                        //calling method to parse json array
                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        //Creating request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding request to the queue
        requestQueue.add(jsonArrayRequest);
    }

    //This method will parse json data
    private void parseData(JSONArray array){
        for(int i = 0; i<array.length(); i++) {
            Model model = new Model();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
//                model.setImageUrl(json.getString(Config.TAG_IMAGE_URL));
                model.setArticleUrl(json.getString(Config.TAG_URLARTICLE));
                model.setPageViews(json.getInt(Config.TAG_PAGEVIEWS));

              /*  model.setRealName(json.getString(Config.TAG_REAL_NAME));
                model.setCreatedBy(json.getString(Config.TAG_CREATED_BY));
                model.setFirstAppearance(json.getString(Config.TAG_FIRST_APPEARANCE));*/

               /* ArrayList<String> powers = new ArrayList<String>();

                JSONArray jsonArray = json.getJSONArray(Config.TAG_POWERS);

                for(int j = 0; j<jsonArray.length(); j++){
                    powers.add(((String) jsonArray.get(j))+"\n");
                }
                superHero.setPowers(powers);*/


            } catch (JSONException e) {
                e.printStackTrace();
            }
            listResultados.add(model);
        }

        //Finally initializing our adapter
        adapter = new CardAdapter(listResultados, context);

        //Adding adapter to recyclerview
        recyclerView.setAdapter(adapter);
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
