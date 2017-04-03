package edu.oswego.lakerparser.parser;


import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.oswego.lakerparser.constants.EndPoints;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

public class LakerScrapper {

    private int termCode;
    private JsonArray terms = new JsonArray();
    private JsonArray subjects = new JsonArray();
    private JsonArray courses = new JsonArray();

    private WebClient webClient;
    private HtmlPage currentPage;

    private int queriesForCourses = 0;

    public LakerScrapper() throws IOException {
        webClient = new WebClient(BrowserVersion.FIREFOX_45);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());

        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        currentPage = webClient.getPage(EndPoints.getSelectTermPage());

    }

    public JsonArray getTerms() throws IOException {
        if(terms.size() == 0) {
            queryForTerms();
        }

        return terms;
    }

    public JsonArray getCourses() throws IOException {
        if(courses.size() == 0) {
            queryForCourses();
        }
        return courses;
    }

    public JsonArray getSubjects() throws IOException {
        if(subjects.size() == 0) {
            queryForSubjects();
        }
        return subjects;
    }

    public void selectTerm(int termCode) throws URISyntaxException, IOException {
        this.termCode = termCode;
        queryForSubjects();
        submitTermForm();
    }

    private void queryForTerms() throws IOException {
        WebResponse response = restRequest(EndPoints.getTermsEndpoint());
        JsonArray element = new JsonParser().parse(response.getContentAsString()).getAsJsonArray();
        terms.addAll(element);
    }

    private void queryForSubjects() throws IOException {
        int page = 1;
        while (true) {
            WebResponse response = restRequest(EndPoints.getSubjectsEndpoint(termCode, page));
            JsonArray array = new JsonParser().parse(response.getContentAsString()).getAsJsonArray();
            if (array != null && array.size() > 0) {
                array.forEach(element -> {
                    if (element != null) {
                        subjects.add(element.getAsJsonObject());
                    }
                });
            } else {
                break;
            }

            page++;
        }
    }

    private void queryForCourses() throws IOException {
        JsonArray subs = getSubjects();
        queriesForCourses = 0;
        for (JsonElement temp : subs) {
            JsonObject subj = temp.getAsJsonObject();
            String subjCode = subj.get("code").getAsString();
            System.out.println("Getting courses for subject:" + subjCode);
            queryCourses(subjCode);
            System.out.println("total queries so far: " + queriesForCourses);
        }
    }

    private void queryCourses(String subject) throws IOException {
        Map<Integer, JsonObject> coursesMap = new HashMap<>();
        int page = 0;

        while (true) {
            resetForm();
            WebResponse response = restRequest(EndPoints.getCoursesEndpoint(subject, termCode, page));

            JsonObject output = new JsonParser().parse(response.getContentAsString()).getAsJsonObject();
            JsonArray array = output != null && output.has("data") ? output.get("data").getAsJsonArray() : null;

            if(array != null && array.size() > 0) {
                array.forEach(element -> {
                    JsonObject course = element.getAsJsonObject();
                    int id = course.get("id").getAsInt();
                    if(!coursesMap.containsKey(id)) {
                        coursesMap.put(id, course);
                    }
                });
            } else {
                break;
            }
            page++;
            queriesForCourses++;
        }

        coursesMap.values().forEach(this.courses::add);
    }

    private void submitTermForm() throws URISyntaxException, IOException {
        if (!webClient.getCurrentWindow().getEnclosedPage().getUrl().toURI().toString().contains(EndPoints.getSelectTermPage())) {
            System.out.println("Going back");
            currentPage = webClient.getPage(EndPoints.getSelectTermPage());
        }

        DomElement comboBox = currentPage.getElementById("s2id_txt_term");
        DomElement a = comboBox.getFirstElementChild();
        a.click();

        DomElement ul = currentPage.getElementById("select2-results-1");

        webClient.waitForBackgroundJavaScriptStartingBefore(1000);

        if (ul.getChildElementCount() > 0) {
            DomElement termDiv = currentPage.getElementById(String.valueOf(termCode));
            termDiv.click();
            webClient.waitForBackgroundJavaScriptStartingBefore(1000);
            currentPage.getElementById("term-go").click();
        } else {
            throw new AssertionError("Nothing in select");
        }

        System.out.println(webClient.getCurrentWindow().getEnclosedPage().getUrl().toURI().toString());

    }

    private void resetForm() throws IOException {
        makeRequest(EndPoints.getResetEndpoint());
    }

    private WebResponse restRequest(String endpoint) throws IOException {
        Page page = makeRequest(endpoint);
        if(page.getWebResponse() != null) {
            if(!page.getWebResponse().getContentType().equals("application/json")) {
                throw new AssertionError("Unexpected response type:".concat(page.getWebResponse().getContentType()));
            }
            return page.getWebResponse();
        }else {
            throw new AssertionError("No response");
        }
    }

    private Page makeRequest(String endpoint) throws IOException {
        WebRequest request = new WebRequest(new URL(endpoint), HttpMethod.GET);
        Page responsePage = webClient.getPage(request);
        if(responsePage == null) {
            throw new AssertionError("No page result");
        }
        return responsePage;
    }

}
