package edu.oswego.lakerparser;


import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class LakerScrapper {

    private static final String SELECT_TERM_PAGE = "https://banner-xe-02.oswego.edu:8443/StudentRegistrationSSB/ssb/term/termSelection?mode=search";

    private String termString;
    private JsonArray subjects;

    private WebClient webClient;
    private HtmlPage currentPage;

    public LakerScrapper() throws IOException {
        webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getCookieManager().setCookiesEnabled(true);
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        currentPage = webClient.getPage(SELECT_TERM_PAGE);

    }

    public JsonArray getTerms(int historyLength) throws IOException {
        String uri = EndPoints.getTermsEndpoint(historyLength);
        System.out.println(uri);
        WebRequest request = new WebRequest(new URL(uri), HttpMethod.GET);

        Page responsePage = webClient.getPage(request);
        WebResponse response = responsePage.getWebResponse();
        if(response.getContentType().equals("application/json")) {

        }else {
            throw new AssertionError("Unexpected content type:".concat(response.getContentType()));
        }



        return null;

    }

    public void setTermString(String termString) {
        this.termString = termString;
        browserSubmitTerm();
    }

    public JsonArray getCourses() {
        throw new AssertionError();
    }

    private void browserSubmitTerm() {

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