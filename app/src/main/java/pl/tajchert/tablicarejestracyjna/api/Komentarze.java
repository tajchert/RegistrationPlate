package pl.tajchert.tablicarejestracyjna.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaltajchert on 28/12/14.
 */
public class Komentarze {
    private String data;
    private String podpis;
    private String tresc;
    private Integer id;
    private Integer ocena;
    private List<Odpowiedzi> odpowiedzi = new ArrayList<Odpowiedzi>();

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

    public List<Odpowiedzi> getOdpowiedzi() {
        return odpowiedzi;
    }

    public void setOdpowiedzi(List<Odpowiedzi> odpowiedzi) {
        this.odpowiedzi = odpowiedzi;
    }

    @Override
    public String toString() {
        return "Komentarze{" +
                "data='" + data + '\'' +
                ", podpis='" + podpis + '\'' +
                ", tresc='" + tresc + '\'' +
                ", id=" + id +
                ", ocena=" + ocena +
                ", odpowiedzi=" + odpowiedzi +
                '}';
    }
}
