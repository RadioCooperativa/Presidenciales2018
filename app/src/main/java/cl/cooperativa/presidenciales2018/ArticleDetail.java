package cl.cooperativa.presidenciales2018;

import java.io.Serializable;

/**
 * Created by innova6 on 14-07-2017.
 */

public class ArticleDetail implements Serializable {

    private String titleDetail,contDetail,imageUrlDetail,cuerpoEnVivo;

    public ArticleDetail() {
    }

    public ArticleDetail(String titleDetail, String contDetail, String imageUrlDetail,String cuerpoEnVivo) {
        this.titleDetail = titleDetail;
        this.contDetail = contDetail;
        this.imageUrlDetail = imageUrlDetail;
        this.cuerpoEnVivo=cuerpoEnVivo;
    }

    public String getCuerpoEnVivo() {
        return cuerpoEnVivo;
    }

    public void setCuerpoEnVivo(String cuerpoEnVivo) {
        this.cuerpoEnVivo = cuerpoEnVivo;
    }

    public String getTitleDetail() {
        // System.out.println("Article Detail, titleDetail: "+titleDetail);
        return titleDetail;
    }

    public void setTitleDetail(String titleDetail) {
        this.titleDetail = titleDetail;
    }

    public String getContDetail() {
        return contDetail;
    }

    public void setContDetail(String contDetail) {
        this.contDetail = contDetail;
    }

    public String getImageUrlDetail() {
        return imageUrlDetail;
    }

    public void setImageUrlDetail(String imageUrlDetail) {
        this.imageUrlDetail = imageUrlDetail;
    }


}
