package cl.cooperativa.presidenciales2018;

/**
 * Created by innova6 on 21-08-2017.
 */

public class ModelNotifications {

    private String id;
    private String title;
    private String description;
    private String extra;

    public ModelNotifications() {
    }

    public ModelNotifications(String id, String title, String description, String extra) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.extra = extra;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
