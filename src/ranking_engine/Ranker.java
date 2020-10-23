package ranking_engine;

import java.io.IOException;
import java.util.*;
import include.MapStringParser;
import include.TextParser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Ranker {
    public static ArrayList<String> run(String input, String query, int most_rel) throws Exception {
        Path output = new Path(input, "query");
        Configuration conf = new Configuration();
        //Save to Configuration the indexer directory and parsed query in form of list of several strings
        conf.set("query.indexer", input);
        conf.set("query.content", TextParser.parse(query));
        FileSystem fs = FileSystem.get(conf);
        //If output directory already exists, delete it operate new query
        if (fs.exists(output))
            fs.delete(output, true);

        Job job = Job.getInstance(conf, "query");
        job.setJarByClass(Ranker.class);
        job.setMapperClass(RankerMapper.class);
        job.setReducerClass(RankerReducer.class);
        job.setOutputKeyClass(DoubleWritable.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(input, "document_vectors"));
        FileOutputFormat.setOutputPath(job, output);

        if (!job.waitForCompletion(true))
            throw new Exception("Error with Ranker class");

        conf = job.getConfiguration();
        fs = FileSystem.get(conf); // Load the Configuration directory where query operated
        /*
            Load to title_map HashMap, which contains all titles and urls of all documents
            Also, load all documents pairs in form(relevance, doc_id) to Arraylist called "outs" in sorted order by relevance
         */
        HashMap<Integer, String> title_map = MapStringParser.HDFS_IS2MAP(fs, new Path(input, "title_urls"));
        ArrayList<Output> outs = readQuery(fs, output);
        fs.delete(output, true); //When you read all output and done with query, delete working directory of query
        fs.close();

        ArrayList<String> answer_docs = new ArrayList<>();
        //Take title and url from title_map Hashmap the most related docs sorted by descending order of relevance
        for (Output o : outs)
            for (String docId : o.docs) {
                if(most_rel == 0) break; most_rel--;
                answer_docs.add(title_map.get(Integer.parseInt(docId)));
            }
        return answer_docs;
    }

    public static class Output {
        double relevance;
        String[] docs;
        Output(String value) {
            String[] relevant_docs = value.split("\t");
            docs = relevant_docs[1].split("~");
            relevance = Double.parseDouble(relevant_docs[0]);
        }
    }
    private static final Comparator<Output> compare = Comparator.comparingDouble(v -> v.relevance);
    private static ArrayList<Output> readQuery(FileSystem fs, Path path) throws IOException {
        ArrayList<Output> outs = new ArrayList<>();
        ArrayList<String> lines = MapStringParser.HDFS2MAP(fs,path);
        for (String line : lines)
            outs.add(new Output(line));
        outs.sort(compare);
        return outs;
    }
}
