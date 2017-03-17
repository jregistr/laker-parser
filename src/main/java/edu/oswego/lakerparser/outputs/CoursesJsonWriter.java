package edu.oswego.lakerparser.outputs;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class CoursesJsonWriter {

    public static void writeToFile(JsonArray allCourses, String fileName) throws IOException {

        Writer writer = new FileWriter(fileName);
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
    }

}
