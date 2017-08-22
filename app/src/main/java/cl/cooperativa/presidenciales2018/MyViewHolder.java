package cl.cooperativa.presidenciales2018;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by innova6 on 14-07-2017.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView titleTxt,dateFechaTxt,dateHoraTxt;
    TextView titleDetail;
    ImageView img;

    public MyViewHolder(View itemView) {
        super(itemView);

        titleTxt= (TextView) itemView.findViewById(R.id.titleTxt);
        titleDetail = (TextView) itemView.findViewById(R.id.userNameDatail);

        //desctxt= (TextView) itemView.findViewById(R.id.descTxt);
        dateFechaTxt= (TextView) itemView.findViewById(R.id.dateFechaTxt);
        dateHoraTxt=(TextView)itemView.findViewById(R.id.dateHoraTxt) ;
        img= (ImageView) itemView.findViewById(R.id.articleImage);

    }
}
