package org.rms.webcrawler;

import java.util.*;

public class SpiderManager {

    private final String seedURL;
    private final Integer maxPagesToCrawl;
    private final Set<String> pagesCrawledOrQueued;
    private final Queue<String> pagesToCrawl;

    public SpiderManager(String seedURL, Integer maxPagesToCrawl) {
        this.seedURL = seedURL;
        this.maxPagesToCrawl = maxPagesToCrawl;
        pagesCrawledOrQueued = new HashSet<>();
        pagesToCrawl = new LinkedList<>();
    }

    public void executeCrawl() {
        addPageToCrawlIfNewAndPageLimitNotReached(seedURL);

        while (!pagesToCrawl.isEmpty()) {
            crawlPage(getNextPageToCrawl());
        }
    }

    private void crawlPage(String pageToCrawl) {
        SpiderWorker spiderWorker = new SpiderWorker(pageToCrawl);
        spiderWorker.crawl();
        pagesCrawledOrQueued.add(pageToCrawl);
        addPagesToCrawlIfNewAndPageLimitNotReached(spiderWorker.getLinksFound());
    }

    private void addPageToCrawlIfNewAndPageLimitNotReached(String link) {
        if (!pagesCrawledOrQueued.contains(link) && !crawledOrQueuedPageLimitReached()) {
            pagesToCrawl.add(link);
            pagesCrawledOrQueued.add(link);
        }
    }

    private void addPagesToCrawlIfNewAndPageLimitNotReached(List<String> linksFound) {
        linksFound.forEach(this::addPageToCrawlIfNewAndPageLimitNotReached);
    }

    private String getNextPageToCrawl() {
        return pagesToCrawl.poll();
    }

    private boolean crawledOrQueuedPageLimitReached() {
        return pagesCrawledOrQueued.size() >= maxPagesToCrawl;
    }

}
