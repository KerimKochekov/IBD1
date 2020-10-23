package indexing_engine.vector_model;

import java.io.IOException;
import java.util.HashMap;
import include.MapStringParser;
import include.TextParser.Id_Text;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class VectorGeneratorMapper extends Mapper<Object, Text, IntWritable, Text> {
    private HashMap<String, Integer> EnumerationMap, CountMap;
    @Override
    public void setup(Context context) throws IOException {
        Configuration conf = context.getConfiguration();
        FileSystem fs = FileSystem.get(conf);
        //Load the Configuration, read from HDFS and load the content of file as HashMap<String,Integer>
        EnumerationMap = MapStringParser.HDFS_SI2MAP(fs, new Path(conf.get("document.ids")));
        CountMap = MapStringParser.HDFS_SI2MAP(fs, new Path(conf.get("document.counts")));
    }
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        Id_Text document_text = new Id_Text(value); //Transform value to doc_id and text
        IntWritable doc_id = new IntWritable(document_text.docId);
        HashMap<String, Integer> document_map = document_text.getWords();
        for (String word : document_map.keySet()) { //for each word of the text
            Integer word_id = EnumerationMap.get(word); //get id
            double word_count = CountMap.get(word).doubleValue(); //get count, total occurrence in all docs
            Double proportion = document_map.get(word).doubleValue() / word_count; //get ratio, inverse value
            // occurrence of in specific doc / occurrence in of word in overall docs
            String pair = word_id.toString() + "=>" + proportion.toString(); //encode Pair as String with separator "=>"
            value.set(pair); context.write(doc_id, value);
        }
    }
}
