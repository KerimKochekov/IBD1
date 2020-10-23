# IBD1

# Usage guide
For running Indexer:

```
$hadoop jar <jar_name>.jar Indexer <path to input directory in HDFS> <path to output directory in HDFS> 
Example: $hadoop jar project3.jar Indexer /EnWikiSmall /indexer_output
```

For running Query:

```
$hadoop jar <jar_name>.jar Query <path to output directory of indexer in HDFS> <query text> <number of most relevant docs> 
Example: $hadoop jar project3.jar Query /indexer_output "Big data technologies" 5
```
