package cl.cooperativa.presidenciales2018;

/**
 * Created by innova6 on 14-07-2017.
 */

public class Model {

    private String imageUrl;
    private String articleUrl;
    private Integer pageViews;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public Integer getPageViews() {
        return pageViews;
    }

    public void setPageViews(Integer pageViews) {
        this.pageViews = pageViews;
    }
}
