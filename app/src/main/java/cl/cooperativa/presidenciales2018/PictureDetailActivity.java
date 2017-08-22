package cl.cooperativa.presidenciales2018;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

/**
 * Created by innova6 on 14-07-2017.
 */

public class PictureDetailActivity extends AppCompatActivity implements Serializable {

    RSSParserDetail rssParserDetail;
    private List<ArticleDetail> mRssFeedList;
    Context c;
    private RssAdapter mRssAdap;
    private ListView mRssListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_picture_detail);
        setElement();
        //showToolbar("",true);

        getWindow().setEnterTransition(new Fade());
        c = getApplicationContext();
    }
    private class RssAdapter extends ArrayAdapter<ArticleDetail> {
        private List<ArticleDetail> rssFeedLst;

        public RssAdapter(Context context, int textViewResourceId, List<ArticleDetail> rssFeedLst) {
            super(context, textViewResourceId, rssFeedLst);
            this.rssFeedLst = rssFeedLst;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            RssHolder rssHolder = null;
            if (convertView == null) {
                view = View.inflate(PictureDetailActivity.this, R.layout.activity_picture_detail, null);
                rssHolder = new RssHolder();
                rssHolder.rssTitleView = (TextView) view.findViewById(R.id.userNameDatail);
                view.setTag(rssHolder);
            } else {
                rssHolder = (RssHolder) view.getTag();
            }
            ArticleDetail rssFeed = rssFeedLst.get(position);
            rssHolder.rssTitleView.setText(rssFeed.getTitleDetail());
            return view;
        }
    }

    static class RssHolder {
        public TextView rssTitleView;
    }
    public void setElement (){
        Article article = (Article) getIntent().getSerializableExtra("article_object");

        //Armando Url para capturar archivo de articulo en servidor
        String tsArticle = article.getTsArticle();
        String baseUrl="https://m.cooperativa.cl/noticias/site/artic/";
        String titleDetail = article.getTitle();
        String dataTs = tsArticle.substring(0,8);
        String aammdd = dataTs;
        String directorioXml="/xml/";
        String finalUrl= tsArticle+".xml";
        String linkXml=baseUrl+aammdd+directorioXml+finalUrl;
        //***********************************************************

       // System.out.println("estoy en PictureDetailActivity y la URL es: "+linkXml);

        new DoRssFeedTask().execute(linkXml);
    }
    public class DoRssFeedTask extends AsyncTask<String, Void, List<ArticleDetail>> {
        ProgressDialog prog;

        @Override
        protected void onPreExecute() {
            prog = new ProgressDialog(PictureDetailActivity.this);
            prog.setMessage("Cargando....");
            prog.show();
        }

        @Override
        protected List<ArticleDetail> doInBackground(String... params) {
            for (String urlVal : params) {
                rssParserDetail = new RSSParserDetail(urlVal);
            }

            mRssFeedList=rssParserDetail.parse();
            return mRssFeedList;
        }

        @Override
        protected void onPostExecute(List<ArticleDetail> result) {
            prog.dismiss();

            System.out.println("PictureDetailActivity onPostExecute");
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Intent intent = new Intent(PictureDetailActivity.this, MyAdapterDetail.class);
                    intent.putExtra("_objeto", (Serializable) mRssFeedList);
                   // System.out.println("PictureDetailActivity en onPostExecute: "+mRssFeedList.size());
                    startActivity(intent);

                }
            });


        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

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

}

