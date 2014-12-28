package pl.tajchert.tablicarejestracyjna.api;

/**
 * Created by michaltajchert on 28/12/14.
 */
public class Odpowiedzi {
    private String data;
    private String podpis;
    private String tresc;
    private Integer id;
    private Integer ocena;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPodpis() {
        return podpis;
    }

    public void setPodpis(String podpis) {
        this.podpis = podpis;
    }

    public String getTresc() {
        return tresc;
    }

    public void setTresc(String tresc) {
        this.tresc = tresc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOcena() {
        return ocena;
    }

    public void setOcena(Integer ocena) {
        this.ocena = ocena;
    }

    @Override
    public String toString() {
        return "Odpowiedzi{" +
                "data='" + data + '\'' +
                ", podpis='" + podpis + '\'' +
                ", tresc='" + tresc + '\'' +
                ", id=" + id +
                ", ocena=" + ocena +
                '}';
    }
}
