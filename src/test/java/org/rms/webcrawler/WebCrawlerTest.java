package org.rms.webcrawler;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.io.Resources;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WebCrawlerTest {

    private static final Integer PORT = 3000;

    private static final String PAGE_WITH_MULTIPLE_LINKS = "http://localhost:3000/PageWithMultipleLinks.html";
    private static final String PAGE_WITH_NO_META_DESCRIPTION = "http://localhost:3000/PageWithNoMetaDescription.html";
    private static final String PAGE_WITH_NO_LINKS = "http://localhost:3000/PageWithNoLinks.html";
    private static final String PAGE_GIVES_404_STATUS = "http://localhost:3000/PageGives404Status.html";
    private static final String INVALID_URL = "InvalidURL.fail";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(PORT);

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    @Before
    public void setup() throws IOException {
        setupSuccessResponse("PageWithMultipleLinks.html");
        setupSuccessResponse("PageWithNoMetaDescription.html");
        setupSuccessResponse("PageWithNoLinks.html");
        setupFailureResponse("PageGives404Status.html");
    }

    private void setupSuccessResponse(String page) throws IOException {
        givenThat(get(urlMatching("/" + page)).willReturn(aResponse()
                .withStatus(200)
                .withBody(Resources.toString(Resources.getResource(page), Charset.defaultCharset()))
                .withHeader("Content-type", "text/html")));
    }

    private void setupFailureResponse(String page) {
        givenThat(get(urlMatching("/" + page)).willReturn(aResponse()
                .withStatus(404)));
    }

    @Test
    public void testPageWithMultipleLinks_PrintsMetaDescription_SavesAllLinks() {
        SpiderWorker spiderWorker = new SpiderWorker(PAGE_WITH_MULTIPLE_LINKS);
        spiderWorker.crawl();
        Assert.assertEquals("Page With Multiple Links - A page with multiple links\r\n", systemOutRule.getLog());
        Assert.assertEquals(4, spiderWorker.getLinksFound().size());
        Assert.assertEquals(PAGE_WITH_MULTIPLE_LINKS, spiderWorker.getLinksFound().get(0));
        Assert.assertEquals(PAGE_WITH_NO_LINKS, spiderWorker.getLinksFound().get(1));
        Assert.assertEquals(PAGE_WITH_NO_LINKS, spiderWorker.getLinksFound().get(2));
        Assert.assertEquals(PAGE_WITH_NO_META_DESCRIPTION, spiderWorker.getLinksFound().get(3));
    }

    @Test
    public void testPageWithNoMetaDescriptionAndLinks_PrintsDefaultMetaDescription_FindsNoLinks() {
        SpiderWorker spiderWorker = new SpiderWorker(PAGE_WITH_NO_META_DESCRIPTION);
        spiderWorker.crawl();
        Assert.assertEquals(
                "Page With No Meta Description - No meta description found\r\n",
                systemOutRule.getLog()
        );
        Assert.assertEquals(0, spiderWorker.getLinksFound().size());
    }

    @Test
    public void testInvalidURL_PrintsWarning() {
        SpiderWorker spiderWorker = new SpiderWorker(INVALID_URL);
        spiderWorker.crawl();
        Assert.assertEquals(
                String.format("URL %s is not valid, please enter a valid URL\r\n", INVALID_URL),
                systemOutRule.getLog()
        );
    }

    @Test
    public void testInaccessibleURL_PrintsWarning() {
        SpiderWorker spiderWorker = new SpiderWorker(PAGE_GIVES_404_STATUS);
        spiderWorker.crawl();
        Assert.assertEquals(
                String.format("Failed to get HTML document for URL: %s\r\n", PAGE_GIVES_404_STATUS),
                systemOutRule.getLog()
        );
    }

    @Test
    public void testPageWithMultipleDuplicateLinks_DoesNotCrawlDuplicateLinks() {
        WebCrawler.main(new String[]{PAGE_WITH_MULTIPLE_LINKS, "10"});
        Assert.assertEquals(
                """
                        Page With Multiple Links - A page with multiple links\r
                        Page With No Links - A page with no links\r
                        Page With No Meta Description - No meta description found\r
                        """,
                systemOutRule.getLog()
        );
    }

    @Test
    public void testCrawlDoesNotExceedMaximumCrawlPagesSpecified() {
        WebCrawler.main(new String[]{PAGE_WITH_MULTIPLE_LINKS, "2"});
        Assert.assertEquals(
                """
                        Page With Multiple Links - A page with multiple links\r
                        Page With No Links - A page with no links\r
                        """, systemOutRule.getLog()
        );
    }

    @Test
    public void testCrawlZeroPages_DoesNotCrawl() {
        WebCrawler.main(new String[]{PAGE_WITH_MULTIPLE_LINKS, "0"});
        Assert.assertEquals("", systemOutRule.getLog()
        );
    }

    @Test
    public void testWrongAmountOfArgsGivesError() {
        WebCrawler.main(new String[]{"arg1", "arg2", "arg3"});
        Assert.assertEquals(
                "Please enter two arguments, a seed URL and the maximum number of pages to crawl\r\n",
                systemOutRule.getLog()
        );
    }

    @Test
    public void testInvalidURLGivesError() {
        WebCrawler.main(new String[]{INVALID_URL, "100"});
        Assert.assertEquals(
                String.format("URL %s is invalid, please enter a valid URL and try again\r\n", INVALID_URL),
                systemOutRule.getLog()
        );
    }

    @Test
    public void testInvalidNumberGivesError() {
        WebCrawler.main(new String[]{PAGE_WITH_MULTIPLE_LINKS, "InvalidNumber"});
        Assert.assertEquals(
                "Max pages to crawl is not an integer, please enter a valid integer\r\n",
                systemOutRule.getLog()
        );
    }

}
