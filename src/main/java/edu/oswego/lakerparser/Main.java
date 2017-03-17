package edu.oswego.lakerparser;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Main {

    public static void main(String[] args) {
        try {
            LakerScrapper lakerScrapper = new LakerScrapper();
            JsonArray terms = lakerScrapper.queryTerms(1);
            JsonObject term = terms.get(0).getAsJsonObject();
            lakerScrapper.setTerm(term.get("description").getAsString(), term.get("code").getAsInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
