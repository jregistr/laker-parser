package edu.oswego.lakerparser;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            LakerScrapper lakerScrapper = new LakerScrapper();
            lakerScrapper.getTerms(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
