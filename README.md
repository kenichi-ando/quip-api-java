Quip Automation API Client for Java
===================================

This is a java client library to use [Quip Automation API](https://salesforce.quip.com/dev/automation/documentation).

## Code Example

```
  // Set your personal access token
  QuipClient.setAccessToken(YOUR_PERSONAL_ACCESS_TOKEN);

  // Get a list of documents recently updated
  QuipThread[] threads = QuipThread.getRecentThreads();
  for (QuipThread thread: threads) { 
    System.out.println(thread.getTitle() + " - " + thread.getLink());
  }

  // Create a new document and insert an image into it
  QuipThread thread = QuipThread.createDocument("Sample document", "Let's start!", null, Format.HTML, Type.DOCUMENT);
  QuipBlob blob = thread.addBlob(new File(IMAGE_FILE_PATH));
  thread.editDocument("Inserting an image...", Format.HTML, null, null);
  thread.editDocument("<img src='" + blob.getUrl() + "'>", Format.HTML, null, null);
```

## Reference

* [Quip Automation API Reference](https://quip.com/api/reference)
* [Quip Automation API Official GitHub Repository](https://github.com/quip/quip-api)
* [Get a Personal Automation API Access Token](https://quip.com/api/personal-token)
