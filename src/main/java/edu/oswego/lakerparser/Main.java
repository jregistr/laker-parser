package edu.oswego.lakerparser;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import edu.oswego.lakerparser.data.Course;
import edu.oswego.lakerparser.outputs.CoursesJsonWriter;
import edu.oswego.lakerparser.outputs.SqlMaker;
import edu.oswego.lakerparser.parser.CourseDataParser;
import edu.oswego.lakerparser.parser.LakerScrapper;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Load data from file (yes) or from my-oswego (no)");
        String load = scanner.nextLine();

        List<Course> allCourses;

        if (yes(load)) {
            System.out.println("Enter path to file.");
            String path = scanner.nextLine();
            System.out.println("Is this raw my oswego data?");
            boolean rawOswegoData = yes(scanner.nextLine());

            JsonReader reader = new JsonReader(new FileReader(path));
            JsonArray courses = new JsonParser().parse(reader).getAsJsonArray();

            if (rawOswegoData) {
                CourseDataParser rawParser = new CourseDataParser(courses);
                JsonArray parsedCourses = rawParser.getParsedCoursesAsJson();
                CoursesJsonWriter.writeToFile(parsedCourses, path.concat(".parsed.json"));
                allCourses = rawParser.getParsedCourses();
            } else {
                allCourses = CourseDataParser.loadParsed(courses);
            }

        } else {
            System.out.println("PROCESSING!");
            LakerScrapper scrapper = new LakerScrapper();
            JsonArray terms = scrapper.getTerms();
            System.out.println("Below are the terms to select from.");
            terms.forEach(System.out::println);
            System.out.println("Type the code of the one you want.");
            int code = Integer.parseInt(scanner.nextLine());
            System.out.println("PROCESSING");
            System.out.println("This will take a few minutes");

            scrapper.selectTerm(code);
            JsonArray rawCourses = scrapper.getCourses();
            CoursesJsonWriter.writeToFile(rawCourses, String.format("%s.raw.json", String.valueOf(code)));

            CourseDataParser rawParser = new CourseDataParser(rawCourses);

            JsonArray parsedCourses = rawParser.getParsedCoursesAsJson();
            CoursesJsonWriter.writeToFile(parsedCourses, String.format("%s.parsed.json", String.valueOf(code)));

            allCourses = rawParser.getParsedCourses();

        }

        System.out.println("All done gathering data!");
        System.out.println("Insert into db?");

        if (yes(scanner.nextLine())) {
            URL url = Main.class.getClassLoader().getResource(".credentials");
            if (url != null) {
                JsonObject creds = new JsonParser().parse(new JsonReader(new FileReader(url.getFile()))).getAsJsonObject();
                SqlMaker.insertData(creds.get("host").getAsString(), creds.get("db").getAsString(),
                        creds.get("u").getAsString(), creds.get("p").getAsString(), allCourses);
            } else {
                System.out.println("Could not find credentials file.");
            }
        }

    }

    private static boolean yes(String input) {
        switch (input) {
            case "yes":
                return true;
            case "no":
                return false;
            default:
                throw new IllegalArgumentException("Unexpected input");
        }
    }

}
