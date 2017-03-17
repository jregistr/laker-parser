package edu.oswego.lakerparser.constants;


public class EndPoints {

    private static final String TERMS_FORMAT = "https://banner-xe-02.oswego.edu:8443/StudentRegistrationSSB/ssb/classSearch/getTerms" +
            "?searchTerm=&offset=1&max=10&_=1489766437259";

    private static final String SUBJECTS_FORMAT = "https://banner-xe-02.oswego.edu:8443/StudentRegistrationSSB" +
            "/ssb/classSearch/get_subject?searchTerm=&term=%d&offset=%d&max=10&_=1488498867997";

    private static final String RESET = "https://banner-xe-02.oswego.edu:8443/StudentRegistrationSSB/ssb/classSearch/resetDataForm";

    private static final String COURSES_FORMAT = "https://banner-xe-02.oswego.edu:8443/StudentRegistrationSSB" +
            "/ssb/searchResults/searchResults?txt_subject=%s&txt_term=%d&startDatepicker=" +
            "&endDatepicker=&pageOffset=%d&pageMaxSize=10&sortColumn=subjectDescription&sortDirection=asc";

    private static final String SELECT_TERM_PAGE =
            "https://banner-xe-02.oswego.edu:8443/StudentRegistrationSSB/ssb/term/termSelection?mode=search";


    public static String getTermsEndpoint() {
        return TERMS_FORMAT;
    }

    public static String getSubjectsEndpoint(int termCode, int page) {
        if(page <= 0)
            page = 1;

        return String.format(SUBJECTS_FORMAT, termCode, page);
    }

    public static String getResetEndpoint() {
        return RESET;
    }

    public static String getCoursesEndpoint(String subject, int termCode, int page) {
        return String.format(COURSES_FORMAT, subject, termCode, page);
    }

    public static String getSelectTermPage() {
        return SELECT_TERM_PAGE;
    }
}
