package edu.oswego.lakerparser;


import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

public class LakerScrapper {

    private String searchId = "s2id_autogen1_search";

    private String ulId = "select2-results-1";
    private String boxContainerId = "s2id_txt_term";

    private String termString;
    private int termCode;
    private JsonArray subjects = new JsonArray();

    private WebClient webClient;
    private HtmlPage currentPage;

    public LakerScrapper() throws IOException {
        webClient = new WebClient(BrowserVersion.FIREFOX_45);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());

        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        currentPage = webClient.getPage(EndPoints.getSelectTermPage());

    }

    public JsonArray queryTerms(int historyLength) throws IOException {
        String uri = EndPoints.getTermsEndpoint(historyLength);
        WebRequest request = new WebRequest(new URL(uri), HttpMethod.GET);

        Page responsePage = webClient.getPage(request);
        WebResponse response = responsePage.getWebResponse();
        System.out.println(response.getContentAsString());
        if (response.getContentType().equals("application/json")) {
            JsonElement element = new JsonParser().parse(response.getContentAsString());
            return element.getAsJsonArray();
        } else {
            throw new AssertionError("Unexpected content type:".concat(response.getContentType()));
        }
    }

    public void setTerm(String termString, int termCode) throws URISyntaxException, IOException {
        this.termString = termString;
        this.termCode = termCode;
        queryForSubjects();
        submitTerm();
    }

    public JsonArray getCourses() {
        return null;
    }

    private void submitTerm() throws URISyntaxException, IOException {
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
            ul.getFirstElementChild().click();
            currentPage.getElementById("term-go").click();
        } else {
            throw new AssertionError("Nothing in select");
        }

        System.out.println(webClient.getCurrentWindow().getEnclosedPage().getUrl().toURI().toString());

    }

    private void queryForSubjects() throws IOException {
        int page = 1;
        while (true) {
            String endpoint = EndPoints.getSubjectsEndpoint(termCode, page);
            System.out.println(endpoint);
            WebRequest request = new WebRequest(new URL(endpoint), HttpMethod.GET);
            Page responsePage = webClient.getPage(request);
            WebResponse response = responsePage.getWebResponse();
            if (response.getContentType().equals("application/json")) {
                System.out.println(response.getContentAsString());
                JsonArray array = new JsonParser().parse(response.getContentAsString()).getAsJsonArray();
                if (array != null && array.size() > 0) {
                    array.forEach(element -> {
                        if(element != null) {
                            subjects.add(element.getAsJsonObject());
                        }
                    });
                } else {
                    break;
                }
            } else {
                throw new AssertionError("Wrong reponse type found");
            }
            page++;
        }
    }

    private void queryCourses() {

    }

}
