package ranking_engine;

import java.io.IOException;
import java.util.HashMap;
import common.MapStrConvert;
import common.TextParser;
import common.TextParser.Id_Vector;
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
        parsed_query = TextParser.getWords(conf.get("query.content"));
        id_map = MapStrConvert.HDFS_SI2MAP(FileSystem.get(conf), new Path(conf.get("query.indexer"), "word_enumeration"));
    }
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        Id_Vector doc_vector = new Id_Vector(value);
        double relevance = 0;
        for (String word : parsed_query.keySet())
            if(id_map.containsKey(word)){
                int word_id = id_map.get(word);
                if (doc_vector.vector.containsKey(word_id))
                    relevance += parsed_query.get(word) * doc_vector.vector.get(word_id);
            }
        value.set(doc_vector.docId.toString());
        context.write(new DoubleWritable(-relevance), value);
    }
}
