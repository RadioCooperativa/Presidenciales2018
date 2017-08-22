package cl.cooperativa.presidenciales2018;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by innova6 on 14-07-2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    // private ImageLoader imageLoader;
    private Context context;

    //List of superHeroes
    List<Model> superHeroes;

    public CardAdapter(List<Model> superHeroes, Context context){
        super();
        //Getting all the superheroes
        this.superHeroes = superHeroes;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listado, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Model model =  superHeroes.get(position);

        // imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        // imageLoader.get(model.getImageUrl(), ImageLoader.getImageListener(holder.imageView, R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));

        // holder.imageView.setImageUrl(model.getImageUrl(), imageLoader);
        holder.textViewName.setText(model.getArticleUrl());
        holder.textViewRank.setText(String.valueOf(model.getPageViews()));
       /* holder.textViewRealName.setText(model.getRealName());
        holder.textViewCreatedBy.setText(model.getCreatedBy());
        holder.textViewFirstAppearance.setText(model.getFirstAppearance());

        String powers = "";

        for(int i = 0; i<model.getPowers().size(); i++){
            powers+= model.getPowers().get(i);
        }

        holder.textViewPowers.setText(powers);*/
    }

    @Override
    public int getItemCount() {
        return superHeroes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        // public NetworkImageView imageView;
        public TextView textViewName;
        public TextView textViewRank;
        //  public TextView textViewRealName;
        //   public TextView textViewCreatedBy;
        //  public TextView textViewFirstAppearance;
        //  public TextView  textViewPowers;

        public ViewHolder(View itemView) {
            super(itemView);
            //  imageView = (NetworkImageView) itemView.findViewById(R.id.imageViewHero);
            textViewName = (TextView) itemView.findViewById(R.id.titleTxt);
            textViewRank= (TextView) itemView.findViewById(R.id.dateHoraTxt);
            //  textViewRealName= (TextView) itemView.findViewById(R.id.textViewRealName);
            //  textViewCreatedBy= (TextView) itemView.findViewById(R.id.textViewCreatedBy);
            //  textViewFirstAppearance= (TextView) itemView.findViewById(R.id.textViewFirstAppearance);
            //   textViewPowers= (TextView) itemView.findViewById(R.id.textViewPowers);
        }
    }
}
