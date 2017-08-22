package cl.cooperativa.presidenciales2018;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by innova6 on 14-07-2017.
 */

public class RSSParserDetail implements Serializable {

    private static Context context;
    //Context context;

    private InputStream urlStream;
    private XmlPullParserFactory factory;
    private XmlPullParser parser;

    private List<ArticleDetail> rssFeedList;
    private ArticleDetail rssFeed;

    private String urlString;
    private String tagName;

    private  String title;
    private String link;
    private String description;
    private String cuerpoEnVivo;

    public static final String ITEM = "artic_data";
    public static final String CHANNEL = "private";
    public static final String TITLE = "_txt_titular";
    public static final String LINK = "fotofija_port_649x365";
    public static final String DESCRIPTION = "vtxt_cuerpo";
    public static final String CUERPOENVIVO = "cuerpo_en_vivo";




    public RSSParserDetail( String urlString ) {
        this.urlString=urlString;
    }
    public  InputStream downloadUrl(String urlString) throws IOException, NoSuchAlgorithmException, KeyManagementException {


        URL url = new URL(urlString);
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

        InputStream stream = con.getInputStream();
        return stream;
    }
    public List<ArticleDetail> parse() {
        try {
            int count = 0;
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
            System.out.println("RSSPerserDetail, urlString: "+urlString);
            urlStream = downloadUrl(urlString);
            parser.setInput(urlStream, null);
            int eventType = parser.getEventType();
            boolean done = false;
            rssFeed = new ArticleDetail();
            rssFeedList = new ArrayList<ArticleDetail>();
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (tagName.equals(ITEM)) {
                            rssFeed = new ArticleDetail();
                        }
                        if (tagName.equals(TITLE)) {
                            title = parser.nextText().toString();

                        }
                        if (tagName.equals(LINK)) {
                            link = parser.nextText().toString();
                            link= link.trim();//quitamos los espacios en blaco de path de conexión.
                            // System.out.println("RSSParserDetail, trae el link foto"+link);
                        }
                        if (tagName.equals(DESCRIPTION)) {
                            description = parser.nextText().toString();
                        }
                        if (tagName.equals(CUERPOENVIVO)) {
                            cuerpoEnVivo = parser.nextText().toString();
                            cuerpoEnVivo=cuerpoEnVivo.trim();
                        }

                        break;
                    case XmlPullParser.END_TAG:
                        if (tagName.equals(CHANNEL)) {
                            done = true;
                        } else if (tagName.equals(ITEM)) {
                            System.out.println("RSSParserDetail, em case XmlPullParser.END_TAG");
                            rssFeed=new ArticleDetail(title,description,link,cuerpoEnVivo);
                            rssFeedList.add(rssFeed);
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rssFeedList;
    }

}
