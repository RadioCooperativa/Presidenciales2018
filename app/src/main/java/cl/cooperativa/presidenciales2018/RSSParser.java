package cl.cooperativa.presidenciales2018;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by innova6 on 14-07-2017.
 */

public class RSSParser extends AsyncTask<Void,Void,Boolean> {

    Context c;
    InputStream is;
    RecyclerView rv;

    ProgressDialog pd;
    ArrayList<Article> articles=new ArrayList<>();

    public RSSParser(Context c, InputStream is, RecyclerView rv) {
        this.c = c;
        this.is = is;
        this.rv = rv;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd=new ProgressDialog(c);
        pd.setTitle("Parse");
        pd.setMessage("Parsing...Please wait");
        pd.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return this.parseRSS();
    }

    @Override
    protected void onPostExecute(Boolean isParsed) {
        super.onPostExecute(isParsed);

        pd.dismiss();
        if(isParsed)
        {
            //BIND
            rv.setAdapter(new MyAdapter(c,articles));

        }else {
            Toast.makeText(c,"Unable To Parse",Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean parseRSS()
    {
        try
        {
            XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
            XmlPullParser parser=factory.newPullParser();

            parser.setInput(is,null);
            int event=parser.getEventType();

            String tagValue=null;
            Boolean isSiteMeta=true;

            articles.clear();
            Article article=new Article();

            do {

                String tagName=parser.getName();

                switch (event)
                {
                    case XmlPullParser.START_TAG:
                        if(tagName.equalsIgnoreCase("item"))
                        {
                            article=new Article();
                            isSiteMeta=false;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        tagValue=parser.getText();
                        break;

                    case XmlPullParser.END_TAG:

                        if(!isSiteMeta)
                        {

                            if(tagName.equalsIgnoreCase("title"))
                            {
                                article.setTitle(tagValue);

                            }else if(tagName.equalsIgnoreCase("description"))
                            {
                                String desc=tagValue;
                                article.setDescription(desc.substring(desc.indexOf("/>")+2));

                            }else if(tagName.equalsIgnoreCase("prontus_ts"))
                            {
                                article.setTsArticle(tagValue);

                            }else if (tagName.equalsIgnoreCase("prontus_horap"))
                            {
                                article.setTsHora(tagValue);
                            }else if (tagName.equalsIgnoreCase("prontus_fechap"))
                            {
                                article.setTsFecha(tagValue);
                            }else if (tagName.equalsIgnoreCase("prontus_foto640"))
                            {
                                article.setImageUrl(tagValue);
                            }

                        }

                        if(tagName.equalsIgnoreCase("item"))
                        {
                            articles.add(article);
                            isSiteMeta=true;
                        }

                        break;
                }

                event=parser.next();

            }while (event != XmlPullParser.END_DOCUMENT);

            return true;

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
