package cl.cooperativa.presidenciales2018;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by innova6 on 14-07-2017.
 */

public class Downloader extends AsyncTask<Void,Void,Object> {
    Context c;
    String urlAddress;
    RecyclerView rv;
    ProgressDialog pd;

    public Downloader(Context c, String urlAddress, RecyclerView rv, Boolean flag) {
        this.c = c;
        this.urlAddress = urlAddress;
        this.rv = rv;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd=new ProgressDialog(c);
        pd.setTitle("Cargando XML");
        pd.setMessage("Parseando...Un Momento Por Favor");
        pd.show();
    }
    @Override
    protected Object doInBackground(Void... params) {
        return downloadData();
    }
    @Override
    protected void onPostExecute(Object data) {
        super.onPostExecute(data);
        pd.dismiss();
        if(data.toString().startsWith("Error"))
        {
            Toast.makeText(c, data.toString(), Toast.LENGTH_SHORT).show();
        }else {
            //PARSE
            new RSSParser(c, (InputStream) data,rv).execute();

        }
    }
    private Object downloadData()
    {
        Object connection=Connector.connect(urlAddress);
        if(connection.toString().startsWith("Error"))
        {
            return connection.toString();

        }

        try
        {

            System.out.println("Downloader downloadData: ");
            HttpsURLConnection con= (HttpsURLConnection) connection;
            int responseCode=con.getResponseCode();
            System.out.println("Downloader Response Code: "+responseCode);
            if(responseCode==200)
            {
                InputStream is=new BufferedInputStream(con.getInputStream());
                return is;
            }
            return ErrorTracker.RESPONSE_EROR+con.getResponseMessage();
        } catch (IOException e) {
            e.printStackTrace();
            return ErrorTracker.IO_EROR;
        }
    }
}