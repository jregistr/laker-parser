package edu.oswego.lakerparser.outputs;


import edu.oswego.lakerparser.data.Course;
import edu.oswego.lakerparser.data.Meeting;
import edu.oswego.lakerparser.data.Section;

import java.sql.*;
import java.util.List;

public class SqlMaker {

    public static void insertData(String host, String db, String uName, String pass, List<Course> courses) throws SQLException {
        String url = String.format("jdbc:mysql://%s:%d/%s?autoReconnect=true&useSSL=false", host, 3306, db);
        Connection connection = DriverManager.getConnection(url, uName, pass);

        for (Course course : courses) {
            insertCourse(connection, getSunyOswegoId(connection), course);
        }

    }

    private static long getSunyOswegoId(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT id FROM schools WHERE name = ?"
        );
        statement.setString(1, "SUNY Oswego");

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {

            long id = resultSet.getLong("id");
            statement.close();
            return id;
        } else {
            statement.close();

            statement = connection.prepareStatement(
                    "INSERT INTO schools (name, country, state, city) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, "SUNY Oswego");
            statement.setString(2, "USA");
            statement.setString(3, "NY");
            statement.setString(4, "Oswego");

            if (statement.executeUpdate() == 0) {
                throw new AssertionError("Failed creating school");
            }

            ResultSet gen = statement.getGeneratedKeys();
            gen.next();
            long id = gen.getLong(1);
            statement.close();
            return id;
        }
    }

    private static void insertCourse(Connection connection, long schoolId, Course course) throws SQLException {
        System.out.println(course.getName());
        PreparedStatement statement = connection.
                prepareStatement("INSERT INTO courses (school_id, name, crn, credits) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);

        statement.setLong(1, schoolId);
        statement.setString(2, course.getName());
        statement.setInt(3, Integer.parseInt(course.getCrn()));
        statement.setInt(4, course.getCredits());

        if (statement.executeUpdate() == 0) {
            throw new AssertionError(String.format("Failed insert course %s", course.getName()));
        }

        ResultSet generated = statement.getGeneratedKeys();
        generated.next();
        long id = generated.getLong(1);

        statement.close();

        for (Section section : course.getSections()) {
            insertSection(connection, id, section);
        }

    }

    private static void insertSection(Connection connection, long courseId, Section section) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO sections (class_id, instructors) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );

        statement.setLong(1, courseId);
        statement.setString(2, section.getInstructors());

        if (statement.executeUpdate() == 0) {
            throw new AssertionError(String.format("Failed to insert section. Course ID:%s", courseId));
        }

        ResultSet generated = statement.getGeneratedKeys();
        generated.next();
        long id = generated.getLong(1);

        statement.close();

        insertMeetings(connection, id, section.getMeetings());

    }

    private static void insertMeetings(Connection connection, long sectionId, List<Meeting> meetings) throws SQLException {
        for (Meeting meeting : meetings) {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO meeting_times (start, end, location, sunday, monday, tuesday, wednesday, thursday, friday, saturday) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            statement.setString(1, meeting.start);
            statement.setString(2, meeting.end);
            statement.setString(3, meeting.location);
            statement.setBoolean(4, meeting.sunday);
            statement.setBoolean(5, meeting.monday);
            statement.setBoolean(6, meeting.tuesday);
            statement.setBoolean(7, meeting.wednesday);
            statement.setBoolean(8, meeting.thursday);
            statement.setBoolean(9, meeting.friday);
            statement.setBoolean(10, meeting.saturday);

            if (statement.executeUpdate() == 0) {
                throw new AssertionError("Failed to insert meeting.");
            }

            ResultSet generated = statement.getGeneratedKeys();
            generated.next();
            long meetingId = generated.getLong(1);

            statement.close();
            insertSectionMeeting(connection, sectionId, meetingId);

        }
    }

    private static void insertSectionMeeting(Connection connection, long sectionId, long meetingId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO sections_meeting_times(section_id, meeting_time_id) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
        );

        statement.setLong(1, sectionId);
        statement.setLong(2, meetingId);

        if (statement.executeUpdate() == 0) {
            throw new AssertionError("Failed to insert meeting.");
        }

        statement.close();
    }

}
