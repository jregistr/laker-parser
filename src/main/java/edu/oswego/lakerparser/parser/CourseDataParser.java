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

    public static List<Course> loadParsed(JsonArray parsed) {
        List<Course> out = new ArrayList<>();
        parsed.forEach(temp -> {
            JsonObject courseJson = temp.getAsJsonObject();

            JsonElement nameElement = courseJson.get("name");
            JsonElement crnElement = courseJson.get("crn");
            JsonElement creditsElement = courseJson.get("credits");

            JsonArray sectionsArray = courseJson.get("sections").getAsJsonArray();

            Course course = new Course(nameElement.isJsonNull() ? null : nameElement.getAsString(),
                    crnElement.getAsString(), creditsElement.getAsInt());

            sectionsArray.forEach(sectionElement -> {
                JsonObject sectionObj = sectionElement.getAsJsonObject();
                JsonElement instElem = sectionObj.get("instructors");

                JsonArray meetingsArray = sectionObj.get("meetings").getAsJsonArray();
                List<Meeting> meetings = new ArrayList<>();
                meetingsArray.forEach(meetingElem -> {
                    JsonObject meetingObj = meetingElem.getAsJsonObject();

                    JsonElement startElem = meetingObj.get("start");
                    JsonElement endElem = meetingObj.get("end");
                    JsonElement locationElem = meetingObj.get("location");

                    Meeting meeting = new Meeting(
                            startElem.isJsonNull() ? null : startElem.getAsString(),
                            endElem.isJsonNull() ? null : endElem.getAsString(),
                            locationElem.isJsonNull() ? null : locationElem.getAsString()
                    );

                    meeting.sunday = meetingObj.get("sunday").getAsBoolean();
                    meeting.monday = meetingObj.get("monday").getAsBoolean();
                    meeting.tuesday = meetingObj.get("tuesday").getAsBoolean();
                    meeting.wednesday = meetingObj.get("wednesday").getAsBoolean();
                    meeting.thursday = meetingObj.get("thursday").getAsBoolean();
                    meeting.friday = meetingObj.get("friday").getAsBoolean();
                    meeting.saturday = meetingObj.get("saturday").getAsBoolean();

                    meetings.add(meeting);

                });

                Section section = new Section(instElem.isJsonNull() ? null : instElem.getAsString(), meetings);
                course.addSection(section);
            });

            out.add(course);
        });
        return out;
    }

    public List<Course> getParsedCourses() {
        return parsedCourses;
    }

    public JsonArray getParsedCoursesAsJson() {
        JsonArray array = new JsonArray();
        getParsedCourses().forEach(course -> array.add(course.toJson()));
        return array;
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
//        String name = jsonObject.get("subjectCourse").getAsString();
        String name = String.format("%s %s", jsonObject.get("subject").getAsString(),
                jsonObject.get("courseNumber").getAsString());
        String crn = jsonObject.get("courseReferenceNumber").getAsString();
        JsonElement credits = jsonObject.get("creditHours");
        return new Course(name, crn, credits.isJsonNull() ? 3 : credits.getAsInt());
    }

    private Section parseSection(JsonObject jsonObject) {
        JsonArray faculty = jsonObject.get("faculty").getAsJsonArray();
        String instructors = faculty.size() > 0 ? parseInstructors(faculty) : "none";
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

            String location;
            if (!building.isJsonNull()) {
                if (room.isJsonNull()) {
                    location = building.getAsString();
                } else {
                    location = String.format("%s %s", building.getAsString(), room.getAsString());
                }
            } else {
                location = null;
            }

            JsonElement start = meeting.get("beginTime");
            JsonElement end = meeting.get("endTime");

            JsonElement sunday = meeting.get("sunday");
            JsonElement monday = meeting.get("monday");
            JsonElement tuesday = meeting.get("tuesday");
            JsonElement wednesday = meeting.get("wednesday");
            JsonElement thursday = meeting.get("thursday");
            JsonElement friday = meeting.get("friday");
            JsonElement saturday = meeting.get("saturday");

            Meeting temp = new Meeting(convertTime(start), convertTime(end), location);

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

    private static String convertTime(JsonElement element) {
        String result = null;
        if (!element.isJsonNull()) {
            String rawTime = element.getAsString();
            if (rawTime.length() == 4) {
                String hoursString = rawTime.substring(0, 2);
                String minutesString = rawTime.substring(2, rawTime.length());

                result = String.valueOf(hoursString) +
                        ":" +
                        String.valueOf(minutesString);
            } else if (rawTime.length() == 0) {
                result = null;
            } else {
                throw new IllegalArgumentException("Unexpected length:" + rawTime.length() + ". String is:" + rawTime);
            }
        }
        return result;
    }

}
