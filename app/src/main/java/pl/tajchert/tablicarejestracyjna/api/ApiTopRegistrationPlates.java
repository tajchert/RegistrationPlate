package pl.tajchert.tablicarejestracyjna.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ApiTopRegistrationPlates {

    public List<Result> results = new ArrayList<Result>();

    public class Result {
        @SerializedName("worst_drivers/_text")
        public String worstDriver;
        @SerializedName("best_drivers/_text")
        public String bestDriver;
    }
}