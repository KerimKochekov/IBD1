# Report of 1st Assignment of IBD

## Team Casablanca member
- Kerim Kochekov

## Github Repo
- Link: [https://github.com/KerimKochekov/IBD1](https://github.com/KerimKochekov/IBD1)

## Report content
- Introduction
- Achieved Results
- Details
- Conclusion
- User guide
- References

## Introduction
The goal of this project to understand and implement simple search engine on Hadoop cluster with MapReduce framework on HDFS. The implementation of project consists of 2 main engines: Indexer engine and Query engine. Indexer engine tends to operate huge amount of data in distributed way with MapReduce technique(Mapping, Shuffling, Reducing) in offline mode. And Query engine work to scan over all ready vectors and find most relevant documents by its relevance value. Here is the relevance value calculation for some doc_vector:
![](https://latex.codecogs.com/gif.latex?r%28q%2Cd%29%20%3D%20%5Csum_%7Bi%3D1%7D%5E%7B%7CV%7C%7D%20q_i%20%5Ccdot%20d_i), where ![](https://latex.codecogs.com/gif.latex?%7CV%7C) stands for the number of distinct words in query.

## Achieved Results
To begin with, we implemented the whole system and it runs successfully. Mostly, we followed all suggestions from the guide [1] regarding the algorithms and overall architecture. In the Appendix there is an instruction on how to run the Indexer and Query operations. The same instruction you could also find in our repository. Here are some points worth to mention:
- **Architecture of the System** is very similar to what was presented in the guide (Fig. 1). 

<p align="center">
<img src="https://user-images.githubusercontent.com/20341995/66276753-39942e00-e89e-11e9-8a9c-e15df9c7c97c.png" width="300" />
</p>

<p align="center"><i>Figure 1. The Search Engine Architecture</i></p>

However, we would have edited this picture. Firstly, there was an important issue of reading a text corpus: we had to be able to read and parse a set of input files containing JSONs to get two structure formats (`docId -> docText` for Indexing Engine and `docId -> (docURL, docTitle)` for Ranker Engine). So, we implemented one more module called CorpusParser which uses MapReduce jobs to parse the corpus into two different outcomes. Secondly, we really did not encounter neccessity of a Vocabulary module so we directly passed outputs from the Word Enumaration and the Document Count to the Indexer. Thirdly, we almost joined the Query Vectorizer, the Relevance Analizator and the Content Extractor into one module because two of them had a basic structure and implementation, whereas only one used MapReduce.
- **Relevance Function** is decided to be a simple one from the guide $$
r(q,d) = \sum_{i: i\in d, i\in q} q_i \cdot d_i.$$ The main advantage of it is its simplicity and ease of implementation compared to BM25 [2]. Indeed, there was no need to complicate the system since the main goal is to explore usage of MapReduce.
- **Text Parser**. We decided that it would be useful to implemented our own text parser instead of using a simple StringTokenizer. Though our parser is not much complicated, it is supposed to prevent some unwanted symbols and substrings to appear.
- **TextToMap and MapToText**. We encountered some issues with writing and reading of a document's TF/IDF weights. So, since we didn't manage to find any "native" solutions, we implemented our own methods to convert and decode the map data structure to/from a text.

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


