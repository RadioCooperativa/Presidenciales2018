package cl.cooperativa.presidenciales2018;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by innova6 on 14-07-2017.
 */

public class MyAdapterDetail extends AppCompatActivity implements Serializable {
    //ArrayList<RSSParserDetail> articles;

    //ArticleDetail articles = new ArticleDetail();
    //ArrayList<ArticleDetail> articles;
    Context c;
    ArrayList<ArticleDetail> articles;


    // MyViewHolderDetail myViewHolderDetail;
//TextView  titleDetail = (TextView) findViewById(R.id.userNameDatail);
    public TextView titleDetail;
    public WebView descDetail;
    public ImageView imageDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ArticleDetail article=articles.get(0);
        // ArticleDetail articles = (ArticleDetail) getIntent().getSerializableExtra("_objeto");

        // System.out.println("MyAdapterDetail tamaño de arreglo que llega es de: "+articles.size());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_detail);
        showToolbar("",true);
        System.out.println("Estoy en el AdapterDetail");
        try {
            setElementDetail();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void setElementDetail() throws MalformedURLException {
//        System.out.println("MyAdapterDetail en setElemetDetail, el tamaño de arreglo es: "+articles.size());
        articles= (ArrayList<ArticleDetail>) getIntent().getSerializableExtra("_objeto");

        ArticleDetail article = articles.get(0);
        String imageUrl=article.getImageUrlDetail();
        String titledet=article.getTitleDetail();
        String desc=article.getContDetail();
        String cuerpo = article.getCuerpoEnVivo();
        String desCuerpo;

        if (cuerpo==null){
            //  System.out.println("MyAdapterDetail, cuerpo vacio");
            desCuerpo=desc;
        }else{
            //  System.out.println("MyAdapterDetail, cuerpo no vacio");
            desCuerpo=cuerpo+desc;
        }

        String cadenaUrl = "https://m.cooperativa.cl"+imageUrl;

        imageDetail=(ImageView) findViewById(R.id.imageHeader);
        Picasso.with(c).load(cadenaUrl).into((ImageView) imageDetail.findViewById(R.id.imageHeader));

        titleDetail = (TextView) findViewById(R.id.userNameDatail);
        titleDetail.setText(titledet);

        descDetail =(WebView) findViewById(R.id.textContentImageDetail) ;
        descDetail.getSettings().setJavaScriptEnabled(true);
        descDetail.setWebChromeClient(new WebChromeClient());
        descDetail.getSettings().setDomStorageEnabled(true);

        descDetail.loadData(desCuerpo,"text/html; charset=UTF-8",null);
        // descDetail.loadData(desc,"text/html; charset=UTF-8",null);


        // System.out.println("Mi cadena URL es: "+cadenaUrl);
        // System.out.println("Mi titulo es: "+titledet);
        //  System.out.println("Mi Detalle es: "+desc);

    }
    private class ImageGetters implements Html.ImageGetter {

        public Drawable getDrawable(String source) {
            int id;
            if (source.equals("ic_account_circle.png")) {
                id = R.drawable.ic_account_circle;
            }
            else {
                return null;
            }

            Drawable d = getResources().getDrawable(id);
            d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
            return d;
        }
    };
    public void showToolbar(String tittle, boolean upButton/*, View view*/){
        /*se crea el metodo showtoolbar para traer toolbar según maqueta de diseño*/
        /*este metodo no debe llevar view porque estamos en cotexto de activity*/

        /*Estamos en contexto de Activity, es por eso que no debe de llevar el código ((AppCompatActivity)getActivity())
        al llevar este código a un fragment si debe llevar ese codigo
         */

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tittle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(upButton);
        CollapsingToolbarLayout collapsingtoolbarlayout =  (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);/*aún no funcional, sea crea para el proximo curso*/

    }

}

