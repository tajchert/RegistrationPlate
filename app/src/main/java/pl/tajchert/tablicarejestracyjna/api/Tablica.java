package pl.tajchert.tablicarejestracyjna.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaltajchert on 28/12/14.
 */
public class Tablica {

    private List<Komentarze> komentarze = new ArrayList<Komentarze>();
    private Integer lapkiGora;
    private Integer lapkiDol;

    public List<Komentarze> getKomentarze() {
        return komentarze;
    }

    public void setKomentarze(List<Komentarze> komentarze) {
        this.komentarze = komentarze;
    }

    public Integer getLapkiGora() {
        return lapkiGora;
    }

    public void setLapkiGora(Integer lapkiGora) {
        this.lapkiGora = lapkiGora;
    }

    public Integer getLapkiDol() {
        return lapkiDol;
    }

    public void setLapkiDol(Integer lapkiDol) {
        this.lapkiDol = lapkiDol;
    }

    @Override
    public String toString() {
        return "Tablica{" +
                " lapkiGora=" + lapkiGora +
                ", lapkiDol=" + lapkiDol +
                ", komentarze=" + komentarze +
                '}';
    }
}
