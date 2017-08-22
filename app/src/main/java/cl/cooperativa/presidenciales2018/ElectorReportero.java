package cl.cooperativa.presidenciales2018;

/**
 * Created by innova6 on 10-08-2017.
 */

public class ElectorReportero {

    private String idtsReportero; //id timestamp elector reportero
    private String nombreReportero; //nombre completo reportero obtenido desde cuenta autenticada
    private String correoReportero; //mail reportero
    private String urlfotoReportero; //ruta de alamcen de foto
    private String descripcionReportero; //texto indicado en electorreportero

    public ElectorReportero() {
    }

    public ElectorReportero(String idtsReportero, String nombreReportero, String correoReportero, String urlfotoReportero, String descripcionReportero) {
        this.idtsReportero = idtsReportero;
        this.nombreReportero = nombreReportero;
        this.correoReportero = correoReportero;
        this.urlfotoReportero = urlfotoReportero;
        this.descripcionReportero = descripcionReportero;
    }

    public String getIdtsReportero() {
        return idtsReportero;
    }

    public void setIdtsReportero(String idtsReportero) {
        this.idtsReportero = idtsReportero;
    }

    public String getNombreReportero() {
        return nombreReportero;
    }

    public void setNombreReportero(String nombreReportero) {
        this.nombreReportero = nombreReportero;
    }

    public String getCorreoReportero() {
        return correoReportero;
    }

    public void setCorreoReportero(String correoReportero) {
        this.correoReportero = correoReportero;
    }

    public String getUrlfotoReportero() {
        return urlfotoReportero;
    }

    public void setUrlfotoReportero(String urlfotoReportero) {
        this.urlfotoReportero = urlfotoReportero;
    }

    public String getDescripcionReportero() {
        return descripcionReportero;
    }

    public void setDescripcionReportero(String descripcionReportero) {
        this.descripcionReportero = descripcionReportero;
    }
}
