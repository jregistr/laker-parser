package edu.oswego.lakerparser.parser;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import edu.oswego.lakerparser.data.Course;
import edu.oswego.lakerparser.data.Meeting;
import edu.oswego.lakerparser.data.Section;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseDataParser {

    private JsonArray allCourses;
    private List<Course> parsedCourses;

    public CourseDataParser(JsonArray allCourses) {
        this.allCourses = allCourses;
        parsedCourses = new ArrayList<>();
        parse();
    }

    public List<Course> getParsedCourses() {
        return parsedCourses;
    }

    private void parse() {
        Map<String, Course> sbjCourseToCourse = new HashMap<>();
        allCourses.forEach(jsonElement -> {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String key;
            Course course = parseCourse(jsonObject);
            if (isLab(jsonObject)) {
                key = String.format("%s %s", jsonObject.get("subjectCourse").getAsString(),
                        jsonObject.get("sequenceNumber").getAsString());
            } else {
                key = jsonObject.get("subjectCourse").getAsString();
            }

            if (!sbjCourseToCourse.containsKey(key)) {
                sbjCourseToCourse.put(key, course);
            }

            course = sbjCourseToCourse.get(key);
            course.addSection(parseSection(jsonObject));
        });

        parsedCourses.addAll(sbjCourseToCourse.values());
    }

    private boolean isLab(JsonObject jsonObject) {
        String seqNum = jsonObject.get("sequenceNumber").getAsString();
        String schType = jsonObject.get("scheduleTypeDescription").getAsString();
        return seqNum.contains("L") || schType.contains("Laboratory");
    }

    private Course parseCourse(JsonObject jsonObject) {
        String name = jsonObject.get("subjectCourse").getAsString();
        String crn = jsonObject.get("courseReferenceNumber").getAsString();
        JsonElement credits = jsonObject.get("creditHours");
        return new Course(name, crn, credits.isJsonNull() ? 0 : credits.getAsInt());
    }

    private Section parseSection(JsonObject jsonObject) {
        JsonArray faculty = jsonObject.get("faculty").getAsJsonArray();
        String instructors = faculty.size() > 0 ? parseInstructors(faculty) : null;
        List<Meeting> meeting = parseMeetings(jsonObject);
        return new Section(instructors, meeting);
    }

    private String parseInstructors(JsonArray faculty) {
        StringBuilder builder = new StringBuilder();
        faculty.forEach(element -> {
            JsonObject inst = element.getAsJsonObject();
            String name = inst.get("displayName").getAsString().replace(",", "");
            builder.append(builder.length() == 0 ? name : ",".concat(name));
        });
        return builder.toString();
    }

    private List<Meeting> parseMeetings(JsonObject jsonObject) {
        List<Meeting> meetings = new ArrayList<>();
        JsonArray meetingFaculties = jsonObject.get("meetingsFaculty").getAsJsonArray();
        meetingFaculties.forEach(element -> {
            JsonObject meetingFaculty = element.getAsJsonObject();
            JsonObject meeting = meetingFaculty.get("meetingTime").getAsJsonObject();

            JsonElement building = meeting.get("building");
            JsonElement room = meeting.get("room");

            String location = room.isJsonNull() ? building.isJsonNull() ? "null" : building.getAsString() :
                    String.format("%s %s", !building.isJsonNull() ? building.getAsString() : "null",
                            !room.isJsonNull() ? room.getAsString() : "null");

            JsonElement start = meeting.get("beginTime");
            JsonElement end = meeting.get("endTime");

            JsonElement sunday = meeting.get("sunday");
            JsonElement monday = meeting.get("monday");
            JsonElement tuesday = meeting.get("tuesday");
            JsonElement wednesday = meeting.get("wednesday");
            JsonElement thursday = meeting.get("thursday");
            JsonElement friday = meeting.get("friday");
            JsonElement saturday = meeting.get("saturday");

            Meeting temp = new Meeting(!start.isJsonNull() ? start.getAsString() : "null",
                    !end.isJsonNull() ? end.getAsString() : "null", location);

            temp.sunday = !sunday.isJsonNull() && sunday.getAsBoolean();
            temp.monday = !monday.isJsonNull() && monday.getAsBoolean();
            temp.tuesday = !tuesday.isJsonNull() && tuesday.getAsBoolean();
            temp.wednesday = !wednesday.isJsonNull() && wednesday.getAsBoolean();
            temp.thursday = !thursday.isJsonNull() && thursday.getAsBoolean();
            temp.friday = !friday.isJsonNull() && friday.getAsBoolean();
            temp.saturday = !saturday.isJsonNull() && saturday.getAsBoolean();
            meetings.add(temp);
        });

        return meetings;
    }

}
