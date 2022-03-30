package org.rms.webcrawler;

import org.apache.commons.validator.routines.IntegerValidator;
import org.apache.commons.validator.routines.UrlValidator;

public class WebCrawler {

    private static String seedURL;
    private static Integer maxPagesToCrawl;

    public static void main(String[] args) {
        if (inputIsValid(args)) {
            setCrawlParameters(args);
            startCrawling();
        }
    }

    private static void startCrawling() {
        SpiderManager spiderManager = new SpiderManager(seedURL, maxPagesToCrawl);
        spiderManager.executeCrawl();
    }

    private static boolean inputIsValid(String[] args) {
        return correctAmountOfArgsGiven(args.length)
                && urlIsValid(args[0])
                && maxPagesToCrawlIsValid(args[1]);
    }

    private static boolean correctAmountOfArgsGiven(Integer amountOfArgs) {

        if (amountOfArgs != 2) {
            System.out.println("Please enter two arguments, a seed URL and the maximum number of pages to crawl");
            return false;
        }

        return true;
    }

    private static boolean urlIsValid(String URL) {
        UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);

        if (!urlValidator.isValid(URL)) {
            System.out.printf("URL %s is invalid, please enter a valid URL and try again%n", URL);
            return false;
        }

        return true;
    }

    private static boolean maxPagesToCrawlIsValid(String maxPagesToCrawl) {

        if (!IntegerValidator.getInstance().isValid(maxPagesToCrawl)) {
            System.out.println("Max pages to crawl is not an integer, please enter a valid integer");
            return false;
        }

        return true;
    }

    private static void setCrawlParameters(String[] args) {
        seedURL = args[0];
        maxPagesToCrawl = Integer.valueOf(args[1]);
    }

}
