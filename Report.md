# Report of 1st Assignment of IBD

## Team Casablanca member
- Kerim Kochekov

## Github Repo
- Link: [https://github.com/KerimKochekov/IBD1](https://github.com/KerimKochekov/IBD1)

## Report content
- Introduction
- Architecture details
- Conclusion
- What can be improved?
- User guide
- References

## Introduction
The goal of this project to understand and implement simple search engine on Hadoop cluster with MapReduce framework on HDFS. The implementation of project consists of 2 main engines: Indexer engine and Query engine. Indexer engine tends to operate huge amount of data in distributed way with MapReduce technique(Mapping, Shuffling, Reducing) in offline mode. And Query engine's work to scan over all ready vectors and find most relevant documents by its relevance value. Here is the relevance value calculation for some doc_vector:<br>
![](https://latex.codecogs.com/gif.latex?r%28q%2Cd%29%20%3D%20%5Csum_%7Bi%3D1%7D%5E%7B%7CV%7C%7D%20q_i%20%5Ccdot%20d_i) <br>
, where ![](https://latex.codecogs.com/gif.latex?%7CV%7C) stands for the number of distinct words in query and ![](https://latex.codecogs.com/gif.latex?q_i) stands for occurence of word with id ![](https://latex.codecogs.com/gif.latex?i).

## Architecture details
During the my limited time until deadline as a single member of team, I tried my hard to follow architecture same as required from us(Figure 1). I Also included bunch of comments to inside of few classes, so you can refer to codes if you interested some part of engines or modules.
<p align="center">
<img src="https://user-images.githubusercontent.com/20341995/66276753-39942e00-e89e-11e9-8a9c-e15df9c7c97c.png" width="300" />
</p>
<p align="center"><i>Figure 1. The Search Engine Architecture</i></p>

- **Indexing Engine**. The first job of "Indexer engine" to call "DocumentParser" class to read and parse all "texts" with "ids" from all JSON files inside the provided directory. After that, the same as the previous job we will call "DocumentParser" with different parseMode to get "urls" and "titles" with "ids" (later it will be needed in the final part of Query engine to show our results titles and urls). Afterward, we will call our WordEnumeration class to give a unique id for all words in our all provided documents with MapReduce technique in a distributed way(it took near 1minute to operate near 500 pages of Wikipedia documents to enumerate, normally if we do naive way, it will take several hours to scan and give unique id). Later, the same as WordEnumeration job, we need to count the occurrence of all words in our documents(like WordCount). Actually, both of these classes have the same job process in the Map and Shuffle process. That is why I implemented a class called CounterMapper for the usage of both to parse the text into several words and set the context as (word, one) format. Finally, after doing all precalculation jobs, we need to call the main part of our architecture, which is called VectorGenerator, reside under the vector_model package with separate VectorMapper and VectorReducer classes. The job of VectorMapper to scan all "texts" with "ids" to create inverse vectors. So, during the Mapping process, we create a vector for each document with values of proportion(occurrence in current doc/occurrence in all docs), and later during the Reduce process, we need to sum up inverse values of all same words and write to context. As result, after running the Indexing engine, you can find 5 new directories in HDFS, which are the outputs of each separate stage: "texts", "title_urls", "word_enumeration", "document_count" and "document_vectors".
- **Query Engine**. The main job of this engine to list all indexed document vectors and find the most relevant documents to a given query text. As I mentioned above in the introduction part that how relevance value calculated for each vector and given query text in this architecture. So in this engine we need to operate single job, which is to get values in the form (relevance, doc_id) by scanning all indexed vectors with dot-product calculations for given query text to get relevance values. The architecture contains the ranking_engine package which is contained 3 classes to give rank for each document, which are: Ranker, RankerMapper, and RankerReducer. Ranker class designed to set configurations and job details to first get the relevance of all documents with RankerMapper and RankerReducer. RankerMapper basically reads the vectors in a form (doc_id, doc_vector), which needed to just do dot-product with vector and occurrence of each word in query text(we do not need to look words which are not contained in query and mapping document). Afterward writing to the context in the form (relevance, doc_id), I need to apply reducer in each of there are several documents with the same relevance. Basically, we can just write 2 documents with the same relevance value in form of String (relevance, doc1~doc2), later we can decode during the output to get each document in a separate format. Finally in the end after getting all values and documents ids. We need to sort output list by relevance value and get top relevant documents with title and urls. Here in this architecture used some precision limits(for example, 5 digits after the dot) to get rid lot computations and ugly view in document_vectors file in Indexing engine.<br>
Also, there is include package which contains mostly used functions by lot of classes to encode/decode String b/w HashMap. Moreover, it contains functions for parsing text, and for managing, keeping values in (key,value) format in known pattern. Also, it contains some functions to interact with HDFS for reading/manipulating documents.

## Conclusion
As result, It was good experience for me to understand Mapreduce framework with popular task "Search enginge". As I mentioned above, during the limited time I investigated lot about syntax of MapReduce and try to write some good pattern codes for in my implementation. I encountered several bugs and issues(especially configuring Hadoop cluster on Windows and installing packages of MapReduce in IDE), but found way to solve them. Despite, it seems some basic "search engine", it learned me lot about Hadoop and MapReduce with package and module manipulations on Java with following patterns.

## What can be improved?
- Calculations and formulas of inverse vectors and ranking_engine can be changed(more complex)
- ML can be used, if we implement in Spark
- Sorting stage of ranking_engine can be also done on Hadoop in distributed way.

# User guide
To run Indexer Engine:

```
$hadoop jar <jar_name>.jar Indexer <path to input directory in HDFS> <path to output directory in HDFS> 
Example: $hadoop jar project3.jar Indexer /EnWikiSmall /indexer_output
```

To run Query Engine:

```
$hadoop jar <jar_name>.jar Query <path to output directory of indexer in HDFS> <query text> <number of most relevant docs> 
Example: $hadoop jar project3.jar Query /indexer_output "Big data technologies" 5
```

## External References
1. Assignment #1. MapReduce. Simple Search Engine. link: [https://hackmd.io/BxoTvclHQFWAS9-2ZpABMQ?view](https://hackmd.io/BxoTvclHQFWAS9-2ZpABMQ?view)
2. Apache Hadoop MapReduce link: [https://hadoop.apache.org/docs/current/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html)


