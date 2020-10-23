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

Implemented few extra classes(located in include) to reside helpfull functions, which used by most of the classes, such as encoding/decpo 

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


