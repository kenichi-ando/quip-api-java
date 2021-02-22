Quip Automation API Client for Java
===================================

This is a java client library to use [Quip Automation API](https://salesforce.quip.com/dev/automation/documentation).

## Steps to use API

1. Download the latest jar in the release
2. Import the jar to your project
3. Get your personal access token from [here](https://quip.com/api/personal-token)
4. Call QuipClient#setAccessToken in your code to set the access token you obtained above

## Code Example

```java
// Set your personal access token
QuipClient.setAccessToken(QUIP_ACCESS_TOKEN);

// Get a list of documents recently updated
QuipThread[] threads = QuipThread.getRecentThreads();
for (QuipThread thread : threads) {
    System.out.println(thread.getId() + ": " + thread.getTitle() + ", " + thread.getLink());
}

// Create a new document
QuipThread thread = QuipThread.createDocument("Document1", "Let's start!", null, Format.HTML, Type.DOCUMENT);

// Insert an image
QuipBlob blob = thread.addBlob(new File(IMAGE_FILE_PATH));
thread.editDocument("Here is the image.", Format.HTML, null, null);
thread.editDocument("<img src='" + blob.getUrl() + "'>", Format.HTML, null, null);

// Add a table
thread.editDocument(QuipTable.createTableHtml(3, 2), QuipThread.Format.HTML, null, QuipThread.Location.APPEND);
QuipTable table = thread.getTableById(thread.getTableIds[0]);
table.updateCellValue(1, 1, "Update cell value");
```

## Reference

* [Quip Automation API Reference](https://quip.com/api/reference)
* [Quip Automation API Official GitHub Repository](https://github.com/quip/quip-api)
