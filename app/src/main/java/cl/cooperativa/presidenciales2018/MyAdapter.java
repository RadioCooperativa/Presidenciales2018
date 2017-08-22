package cl.cooperativa.presidenciales2018;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by innova6 on 14-07-2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    Context c;
    ArrayList<Article> articles;


    public MyAdapter(Context c, ArrayList<Article> articles) {
        this.c = c;
        this.articles = articles;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(c).inflate(R.layout.model,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Article article=articles.get(position);
        System.out.println("MyAdater, position es: "+position);
        String title=article.getTitle();
        //String desc=article.getDescription();
        String dateFecha=article.getTsFecha();
        String dateHora=article.getTsHora();
        String imageUrl=article.getImageUrl();
        final String tsarticle=article.getTsArticle();


        holder.titleTxt.setText(title);
        holder.dateFechaTxt.setText(dateFecha);
        holder.dateHoraTxt.setText(dateHora);


        String baseUrl = "http://www.cooperativa.cl";
        String cadenaUrl = baseUrl+imageUrl;

        // Log.i("valor total cadena",": "+cadenaUrl);
        Picasso.with(c).load(cadenaUrl).into(holder.img);

        holder.img.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(c, PictureDetailActivity.class);

                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){

                    Explode explode = new Explode();
                    explode.setDuration(1000);
                    //si estuvieramos en una actividad no sería necesario agregar activity.getWindow, solo getWindow
                    //activity.getWindow()

                    //como personalizamos la duración de la transición(el objeto Explode), debemos colocar el nombre de la clase instanciada,
                    // .setExitTransition(explode);
                    // de lo contrario solo sería .setExitTransition(new Explode()); y toma los valores por defecto de Explode.

                    //  c.getWindow().setExitTransition(explode);
                    // activity.startActivity(intent, ActivityOptionsCompat.
                    //       makeSceneTransitionAnimation(activity,v,activity.getString(R.string.transitonname_picture))
                    //       .toBundle());



                    //  Log.i("el ts es "," :"+tsarticle.toString());
                    intent.putExtra("article_object", article);
                    c.startActivity(intent);
                }else {
                    c.startActivity(intent);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return articles.size();
    }

}
