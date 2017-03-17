package edu.oswego.lakerparser.data;


import com.google.gson.JsonObject;

public class Meeting {

    public final String start;
    public final String end;
    public final String location;

    public boolean sunday;
    public boolean monday;
    public boolean tuesday;
    public boolean wednesday;
    public boolean thursday;
    public boolean friday;
    public boolean saturday;

    public Meeting(String start, String end, String location) {
        this.start = start;
        this.end = end;
        this.location = location;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("start", start);
        object.addProperty("end", end);
        object.addProperty("location", location);

        object.addProperty("sunday", sunday);
        object.addProperty("monday", monday);
        object.addProperty("tuesday", tuesday);
        object.addProperty("wednesday", wednesday);
        object.addProperty("thursday", thursday);
        object.addProperty("friday", friday);
        object.addProperty("saturday", saturday);

        return object;
    }

}
