# Report of 1st Assignment of IBD

## Team Casablanca member
- Kerim Kochekov

## Github Repo
- Link: [https://github.com/KerimKochekov/IBD1](https://github.com/KerimKochekov/IBD1)

## Report content
- Introduction
- Architecture details
- Conclusion
- User guide
- References

## Introduction
The goal of this project to understand and implement simple search engine on Hadoop cluster with MapReduce framework on HDFS. The implementation of project consists of 2 main engines: Indexer engine and Query engine. Indexer engine tends to operate huge amount of data in distributed way with MapReduce technique(Mapping, Shuffling, Reducing) in offline mode. And Query engine's work to scan over all ready vectors and find most relevant documents by its relevance value. Here is the relevance value calculation for some doc_vector:<br>
![](https://latex.codecogs.com/gif.latex?r%28q%2Cd%29%20%3D%20%5Csum_%7Bi%3D1%7D%5E%7B%7CV%7C%7D%20q_i%20%5Ccdot%20d_i) <br>
, where ![](https://latex.codecogs.com/gif.latex?%7CV%7C) stands for the number of distinct words in query and ![](https://latex.codecogs.com/gif.latex?q_i) stands for occurence of word with id ![](https://latex.codecogs.com/gif.latex?i).

## Architecture details
During my limited time as single member of team, I tried my hard to follow architecture same as required from us(Fig. 1). I Also included bunch of comments to inside of few classes, so you can refer to codes if you interested some part of engines or modules.
<p align="center">
<img src="https://user-images.githubusercontent.com/20341995/66276753-39942e00-e89e-11e9-8a9c-e15df9c7c97c.png" width="300" />
</p>
<p align="center"><i>Figure 1. The Search Engine Architecture</i></p>

- **Indexing Engine**. The first job of "Indexer engine" to call "DocumentParser" class to read and parse all "texts" with "ids" from all JSON files inside the provided directory. After that, the same as the previous job we will call "DocumentParser" with different parseMode to get "urls" and "titles" with "ids" (later it will be needed in the final part of Query engine to show our results titles and urls). Afterward, we will call our WordEnumeration class to give a unique id for all words in our all provided documents with MapReduce technique in a distributed way(it took near 1minute to operate near 500 pages of Wikipedia documents to enumerate, normally if we do naive way, it will take several hours to scan and give unique id). Later, the same as WordEnumeration job, we need to count the occurrence of all words in our documents(like WordCount). Actually, both of these classes have the same job process in the Map and Shuffle process. That is why I implemented a class called CounterMapper for the usage of both to parse the text into several words and set the context as (word, one) format. Finally, after doing all precalculation jobs, we need to call the main part of our architecture, which is called VectorGenerator, reside under the vector_model package with separate VectorMapper and VectorReducer classes. The job of VectorMapper to scan all "texts" with "ids" to create inverse vectors. So, during the Mapping process, we create a vector for each document with values of proportion(occurrence in current doc/occurrence in all docs), and later during the Reduce process, we need to sum up inverse values of all same words and write to context. As result, after running the Indexing engine, you can find 5 new directories in HDFS, which are the outputs of each separate stage: "texts", "title_urls", "word_enumeration", "document_count" and "document_vectors".
- **Query Engine**.

## Conclusion
In conclusion, we had good experience working with MapReduce framework on an interesting task - a search engine. During the workflow, we encountered several issues and found solutions to. Though the system we created is quite simple, now it seems to be not much difficult to upgrade it module by module.

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


