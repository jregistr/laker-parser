package edu.oswego.lakerparser.data;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Course {

    private String name;
    private String crn;
    private int credits;

    private List<Section> sections;

    public Course(String name, String crn, int credits) {
        this.name = name;
        this.crn = crn;
        this.credits = credits;
        sections = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getCrn() {
        return crn;
    }

    public int getCredits() {
        return credits;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("name", name);
        object.addProperty("crn", crn);
        object.addProperty("credits", credits);

        JsonArray sectionsBuilder = new JsonArray();
        sections.forEach(section -> sectionsBuilder.add(section.toJson()));

        object.add("sections", sectionsBuilder);
        return  object;
    }

}
