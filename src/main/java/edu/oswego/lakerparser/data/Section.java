package edu.oswego.lakerparser.data;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class Section {

    private String instructors;

    private List<Meeting> meetings;

    public Section(String instructors, List<Meeting> meetings) {
        this.instructors = instructors;
        this.meetings = meetings;
    }

    public String getInstructors() {
        return instructors;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }

    public JsonObject toJson() {
        JsonArray meetingsBuilder = new JsonArray();
        meetings.forEach(meeting -> meetingsBuilder.add(meeting.toJson()));

        JsonObject object = new JsonObject();

        object.addProperty("instructors", instructors);
        object.add("meetings", meetingsBuilder);

        return object;
    }
}
