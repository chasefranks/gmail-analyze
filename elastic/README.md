# Testing Elasticsearch

## Importing Test Data

Let's first create our index and mapping

```
curl -XPUT localhost:9200/gmail_messages?pretty --data-binary @mapping.json
```

Then we use the `_bulk` API to populate our index with some test data:

```
curl -XPOST localhost:9200/_bulk?pretty --data-binary @messages.json
```

## Querying the Test Dataset

The queries folder has some interesting queries to get you started. To run any of the queries, use curl passing
a request body in with the `--data-binary` flag. For example,

```
curl localhost:9200/gmail_messages/messages/_search?pretty --data-binary @queries/query1.json
```

## Starting Over

To clean up, just delete the index

```
curl -XDELETE localhost:9200/gmail_messages
```
