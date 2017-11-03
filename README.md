# Gmail Analyzer

## tl;dr

Open the file `config.yaml` in the `/src/main/resources` directory and configure the location of your Elasticsearch cluster. For example,

```yml
---
elastic:
  host: "localhost"
  port: 9300
  mapping: "elastic/mapping.json"
  
gmail:
  user: "me"
 ```
 
 Make sure Elasticsearch is running, and run
 
 ```
 gradle run
 ```
 
 to create the index and populate it with message meta data from the user's Gmail inbox.
 
## Creating the Index and Mapping
 
These steps are done automatically by the `gradle run` command, but if you want to create the index and mapping manually, first run
 
 ```
 curl -XPUT localhost:9200/gmail_messages
 ```
 
 to create the index. Then PUT the mapping with
 
 ```
 curl -XPUT localhost:9200/gmail_messages/_mapping/message --data-binary @elastic/mapping.json
 ```
 
 You now have the index and mapping created, but there is no data yet. You can either import the test data using `_bulk` API
 
 
 ```
 curl -XPUT localhost:9200/_bulk --data-binary @elastic/messages.json
 ```
 
 or import data from your actual GMail account by running 
 
 ```
 gradle run
 ```
 
 