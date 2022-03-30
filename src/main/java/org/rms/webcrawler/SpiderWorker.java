package org.rms.webcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SpiderWorker {

    // We are masquerading as GoogleBot to help prevent crawl limitations
    private static final String USER_AGENT = "Googlebot/2.1 (+http://www.google.com/bot.html)";

    private final List<String> linksFound = new LinkedList<>();
    private final String pageToCrawl;
    private Document htmlDocument;

    public SpiderWorker(String pageToCrawl) {
        this.pageToCrawl = pageToCrawl;
    }

    public void crawl() {
        try {
            htmlDocument = getHTMLDocument(pageToCrawl);
            performCrawlTasks();
        } catch (IOException e) {
            System.out.printf("Failed to get HTML document for URL: %s%n", pageToCrawl);
        } catch (IllegalArgumentException e) {
            System.out.printf("URL %s is not valid, please enter a valid URL%n", pageToCrawl);
        }
    }

    private Document getHTMLDocument(String pageURL) throws IOException {
        return Jsoup.connect(pageURL).userAgent(USER_AGENT).get();
    }

    private void performCrawlTasks() {
        outputTitleAndMetaDescription();
        addPageLinksToLinksFound();
    }

    private void outputTitleAndMetaDescription() {
        System.out.printf("%s - %s%n", getPageTitle(), getPageDescriptionFromMetaDataOrDefault());
    }

    private String getPageTitle() {
        return htmlDocument.title();
    }

    private String getPageDescriptionFromMetaDataOrDefault() {
        return Optional.ofNullable(htmlDocument.selectFirst("meta[name=description]"))
                .map(element -> element.attr("content"))
                .orElse("No meta description found");
    }

    private void addPageLinksToLinksFound() {
        getAllLinksOnPage().forEach(this::AddLinkToLinksFound);
    }

    private Elements getAllLinksOnPage() {
        return htmlDocument.select("a[href]");
    }

    private void AddLinkToLinksFound(Element linkElement) {
        linksFound.add(getURLFromLinkElement(linkElement));
    }

    private String getURLFromLinkElement(Element link) {
        return link.absUrl("href");
    }

    public List<String> getLinksFound() {
        return linksFound;
    }

}
