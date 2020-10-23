package ranking_engine;

import java.io.IOException;
import java.util.HashMap;
import include.MapStringParser;
import include.TextParser;
import include.TextParser.Id_Vector;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class RankerMapper extends Mapper<Object, Text, DoubleWritable, Text> {
    private HashMap<String, Integer> id_map, parsed_query;
    @Override
    public void setup(Context context) throws IOException {
        Configuration conf = context.getConfiguration();
        //Load the Configuration, load the Hashmaps contains query and ids of words of entire vocabulary
        parsed_query = TextParser.getWords(conf.get("query.content"));
        id_map = MapStringParser.HDFS_SI2MAP(FileSystem.get(conf), new Path(conf.get("query.indexer"), "word_enumeration"));
    }
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        Id_Vector doc_vector = new Id_Vector(value);
        double relevance = 0;
        for (String word : parsed_query.keySet())
            if(id_map.containsKey(word)){ //for each word of query text, first check is word included in vocabulary
                int word_id = id_map.get(word);
                if (doc_vector.vector.containsKey(word_id))// check word contains in our current specific document
                    relevance += parsed_query.get(word) * doc_vector.vector.get(word_id); //if yes,
                    //add to relevance value occurrence of word in query multiplied by inverse value of word in document
            }
        value.set(doc_vector.docId.toString());
        context.write(new DoubleWritable(-relevance), value); //Finally, write in form (-relevance, doc_id)
        //In order to get maximum value when we sort in ascending order
    }
}
