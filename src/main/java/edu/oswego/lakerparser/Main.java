package edu.oswego.lakerparser;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import edu.oswego.lakerparser.outputs.CoursesJsonWriter;
import edu.oswego.lakerparser.parser.CourseDataParser;
import edu.oswego.lakerparser.parser.LakerScrapper;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        try {
            LakerScrapper lakerScrapper = new LakerScrapper();
            JsonArray terms = lakerScrapper.getTerms();

            JsonObject term = terms.get(1).getAsJsonObject();

            lakerScrapper.selectTerm(term.get("code").getAsInt());

            JsonArray allCourses = lakerScrapper.getCourses();
            CoursesJsonWriter.writeToFile(allCourses);

        } catch (Exception e) {
            e.printStackTrace();
        }


//        JsonReader reader = new JsonReader(new FileReader(
//                Main.class.getClassLoader().getResource("spring2017.json").getFile()
//        ));
//
//        JsonArray courses = new JsonParser().parse(reader).getAsJsonArray();
//
//        CourseDataParser parser = new CourseDataParser(courses);
//
//        JsonArray all = new JsonArray();
//        parser.getParsedCourses().forEach(course -> all.add(course.toJson()));
//
//        System.out.println(all.toString());

    }

}
