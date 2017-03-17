package edu.oswego.lakerparser;


import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.Keyboard;
import com.gargoylesoftware.htmlunit.javascript.host.event.KeyboardEvent;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.xml.ws.Endpoint;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class LakerScrapper {

    private String searchId = "s2id_autogen1_search";

    private String ulId = "select2-results-1";
    private String boxContainerId = "s2id_txt_term";

    private String termString;
    private int termCode;
    private JsonArray subjects;

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

    public JsonArray getTerms(int historyLength) throws IOException {
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

    private void browserSubmitTerm() throws URISyntaxException, IOException {

        webClient.getCookieManager().addCookie(new Cookie("banner-xe-02.oswego.edu", "JSESSIONID", "B8D7A124412436B8892D1A6DE0B8A0B0", "/StudentRegistrationSSB/", 1, true));

        String coursesUri = "https://banner-xe-02.oswego.edu:8443/StudentRegistrationSSB/ssb/searchResults/searchResults?txt_subject=ACC&txt_term=201702&startDatepicker=&endDatepicker=&pageOffset=0&pageMaxSize=10&sortColumn=subjectDescription&sortDirection=asc";
        WebRequest request = new WebRequest(new URL(coursesUri), HttpMethod.GET);

        Page responsePage = webClient.getPage(request);
        WebResponse response = responsePage.getWebResponse();

        System.out.println(response.getContentAsString());


//        if (!webClient.getCurrentWindow().getEnclosedPage().getUrl().toURI().toString().contains(EndPoints.getSelectTermPage())) {
//            currentPage = webClient.getPage(EndPoints.getSelectTermPage());
//        }
//
//        HtmlElement termDateSel = (HtmlElement) currentPage.getElementById("term-date-selection");
//        termDateSel.click();
//
//
//        while (true) {
//            termDateSel.type("\t");
//            webClient.waitForBackgroundJavaScriptStartingBefore(2000);
//            termDateSel.type(" ");
//
//            System.out.println(EndPoints.getSelectTermPage());
//
//            if (webClient.getCurrentWindow().getEnclosedPage().getUrl().toURI().toString().contains(EndPoints.getSelectTermPage())) {
//                System.out.println("haven't left yet");
//                webClient.waitForBackgroundJavaScriptStartingBefore(2000);
//            } else {
//                System.out.println("OUT!!!");
//                System.out.println(webClient.getCurrentWindow().getEnclosedPage().getUrl());
//                break;
//            }
//
//        }


//        FileWriter writer = new FileWriter("out.html");
//        writer.write(((HtmlPage)webClient.getCurrentWindow().getEnclosedPage()).asXml());
//        writer.flush();


//        String coursesUri = "https://banner-xe-02.oswego.edu:8443/StudentRegistrationSSB/ssb/searchResults/searchResults?txt_subject=ACC&txt_term=201702&startDatepicker=&endDatepicker=&pageOffset=0&pageMaxSize=10&sortColumn=subjectDescription&sortDirection=asc";
//        WebRequest request = new WebRequest(new URL(coursesUri), HttpMethod.GET);
//
//        Page responsePage = webClient.getPage(request);
//        WebResponse response = responsePage.getWebResponse();
//
//        webClient.getCookieManager().getCookies().forEach(cookie -> {
//            System.out.println(cookie.getDomain() + " --- " + cookie.getName() + " = " + cookie.getValue());
//        });
//
//        System.out.println(response.getContentAsString());


//        termDateSel.type(KeyboardEvent.DOM_VK_TAB);
//        termDateSel.type(KeyboardEvent.DOM_VK_SPACE);
//
//        termDateSel.type(KeyboardEvent.DOM_VK_TAB);
//        termDateSel.type(KeyboardEvent.DOM_VK_SPACE);

//        System.out.println(currentPage.getFocusedElement());


//        HtmlElement termDateSel = (HtmlElement)currentPage.getElementById("term-date-selection");
//        termDateSel.click();
//
//        System.out.println(currentPage.getElementById("notification-center").getTextContent());
//
//        System.out.println(currentPage.getElementById("term-go"));
//        currentPage.getElementById("term-go").click();
//
//        webClient.waitForBackgroundJavaScript(4000);
//        System.out.println(currentPage.getElementById("notification-center").getTextContent());


//        System.out.println(currentPage.getByXPath("/div[class='notification-item-message vertical-align']"));


//        currentPage.tabToNextElement();
////        termDateSel.type(KeyboardEvent.DOM_VK_TAB);
////        termDateSel.type(KeyboardEvent.DOM_VK_SPACE);
//
//        webClient.waitForBackgroundJavaScript(4000);
//
////        HtmlElement selectionResults = (HtmlElement) currentPage.getElementById("select2-result-label-2");
//
//        System.out.println(currentPage.getFocusedElement());


//        currentPage.tabToNextElement();
//        currentPage.tabToNextElement();
//        currentPage.tabToNextElement();
//        currentPage.tabToNextElement();
//        currentPage.tabToNextElement();
////        currentPage.tabToNextElement();
//
//        System.out.println(currentPage.getFocusedElement().getId());


//        String courseEnd = EndPoints.getCoursesEndpoint("CSC", 201702, 1);
//
//        WebRequest request = new WebRequest(new URL(courseEnd), HttpMethod.GET);
//        Page t = webClient.getPage(request);


        // System.out.println(t.getWebResponse().getContentAsString());


//        DomElement container = currentPage.getElementById(boxContainerId);
//        container.click();
//        webClient.waitForBackgroundJavaScript(3000);
//
//        DomElement other = currentPage.getElementById(ulId);
//
//        System.out.println(container.toString());
//        System.out.println(other);
//        other.getChildElements().forEach(System.out::println);

//        DomElement termDateSel = currentPage.getElementById("s2id_txt_term");
//        termDateSel.click();


    }

}

//        Set<Cookie> cookieHashSet = new HashSet<>();
//        cookieHashSet.addAll(webClient.getCookieManager().getCookies());
//        Cookie[] cookies = cookieHashSet.toArray(new Cookie[cookieHashSet.size()]);
//
//        StringBuilder cookieBuilder = new StringBuilder();
//
//        for (int i = 0; i < cookies.length; i++) {
//            Cookie cookie = cookies[i];
//            if(i == 0) {
//                cookieBuilder.append(cookie.getName()).append("=").append(cookie.getValue());
//            }else {
//                cookieBuilder.append(";").append(cookie.getName()).append("=").append(cookie.getValue());
//            }
//        }
//
//        request.setAdditionalHeader("Cookie", cookieBuilder.toString());