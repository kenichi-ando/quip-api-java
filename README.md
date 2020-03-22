Quip Automation API Client for Java
===================================

This is a java client library to use [Quip Automation API](https://salesforce.quip.com/dev/automation/documentation).

## Steps to use API

1. Get your personal access token from [here](https://quip.com/api/personal-token)
2. Call QuipClient#setAccessToken to set the access token you obtained above

## Code Example

```
	void example() throws Exception {
		// Set your personal access token
		QuipClient.setAccessToken(QUIP_ACCESS_TOKEN);

		// Get a list of documents recently updated
		QuipThread[] threads = QuipThread.getRecentThreads();
		for (QuipThread thread : threads) {
			System.out.println(thread.getId() + ": " + thread.getTitle() + ", " + thread.getLink());
		}

		// Create a new document and insert an image into it
		QuipThread thread = QuipThread.createDocument("Document1", "Let's start!", null, Format.HTML, Type.DOCUMENT);
		QuipBlob blob = thread.addBlob(new File(IMAGE_FILE_PATH));
		thread.editDocument("Here is the image.", Format.HTML, null, null);
		thread.editDocument("<img src='" + blob.getUrl() + "'>", Format.HTML, null, null);

		// Delete the document
		thread.delete();
	}
```

## Reference

* [Quip Automation API Reference](https://quip.com/api/reference)
* [Quip Automation API Official GitHub Repository](https://github.com/quip/quip-api)