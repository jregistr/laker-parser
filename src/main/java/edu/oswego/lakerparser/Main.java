package edu.oswego.lakerparser;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.Writer;
import java.util.Iterator;

public class Main {

    public static void main(String[] args) {
        try {
            LakerScrapper lakerScrapper = new LakerScrapper();
            JsonArray terms = lakerScrapper.getTerms();
            JsonObject term = terms.get(0).getAsJsonObject();

            lakerScrapper.selectTerm(term.get("description").getAsString(), term.get("code").getAsInt());
            JsonArray allCourses = lakerScrapper.getCourses();

            Writer writer = new FileWriter("courses.json");
            writer.write("[\n");
            Iterator<JsonElement> objectIterator = allCourses.iterator();
            if (objectIterator.hasNext()) {
                writer.write(objectIterator.next().toString());
            }

            while (objectIterator.hasNext()) {
                writer.write("\n");
                writer.write(",");
                writer.write(objectIterator.next().toString());
            }

            writer.write("]");
            writer.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
