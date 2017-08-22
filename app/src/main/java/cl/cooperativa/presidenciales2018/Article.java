package cl.cooperativa.presidenciales2018;

import java.io.Serializable;

/**
 * Created by innova6 on 14-07-2017.
 */

public class Article implements Serializable {

    String title, description,tsArticle, tsHora,tsFecha, imageUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTsArticle() {
        return tsArticle;
    }

    public void setTsArticle(String tsArticle) {
        this.tsArticle = tsArticle;
    }

    public String getTsHora() {
        return tsHora;
    }

    public void setTsHora(String tsHora) {
        this.tsHora = tsHora;
    }

    public String getTsFecha() {
        return tsFecha;
    }

    public void setTsFecha(String tsFecha) {
        this.tsFecha = tsFecha;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
