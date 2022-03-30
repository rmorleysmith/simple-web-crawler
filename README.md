# Simple Java Web Crawler
***

## Overview
This is a simple web crawler written in Java 17.
Given a seed URL and maximum number of pages, it will begin crawling from the seed URL,
printing each page's title and meta description to the console.

## Instructions
To run the web crawler, first start by cloning the repository and installing Maven if you don't already have it.

You can execute the program with default arguments using the following command for Maven:
```
mvn compile exec:java
```
This will set the crawl parameters as found in the pom.xml:
```
<arguments>
    <argument>https://crawler-test.com/</argument>
    <argument>500</argument>
</arguments>
```
The crawler will crawl using the seed URL https://crawler-test.com/ to a maximum of 500 pages crawled.  

To specify your own parameters, you can override the default arguments by using the following command:  
```
mvn compile exec:java -Dexec.arguments=SeedURL,MaxPagesToCrawl
```
Where SeedURL should be replaced with the seed URL, and MaxPagesToCrawl should be replaced with the crawl limit.  
  
For example:
```
mvn compile exec:java -Dexec.arguments=https://crawler-test.com/,500
```

## Things To Improve

### Multi-Threading
A great way of increasing the performance of a program like this is to use multi-threading. That was we can enlist the
help of multiple threads to crawl pages.  
  
The way I'd like to implement this in future is to have the SpiderManager allocate pages to crawl to SpiderWorkers,
which will each run in a separate thread, allowing multiple pages to be crawled concurrently.

### Robots.txt Handling
Adding handling for robots.txt is something which needs to be done to prevent crawling pages which websites do not want
us to crawl, or to prevent overloading the website with requests.

We do this by parsing the websites robots.txt file if they have one, which tells us which URLs we're not allowed
to crawl.

### Redirect Handling
Currently, if a link redirects to a page we have already crawled,
then we have a scenario where we crawl the same page twice.

### Domain Limiting
We do not currently impose any restrictions on pages to crawl when it comes to the domain, this means that we will crawl
external links just the same as we do internal links.

This can cause us to go down a rabbit hole in terms of crawling a website we're not interested in.

## Development Notes

### JavaDocs & Comments
I have not included any JavaDocs in this program, due to the fact that the program is small, and I have endeavored
to name and structure methods in such a way that they are extremely self-explanatory.

Similarly, you will find minimal comments in the code, and should struggle to find a place to put a comment which would
not be redundant.

If whilst reading the code, you think to yourself
"this could have done with a comment/JavaDoc because it isn't entirely clear what's going on",
then I would argue the shortfall is due to my code structure, and not the lack of comment/documentation.

In a professional setting, JavaDocs/comments should be written as per company guidelines.