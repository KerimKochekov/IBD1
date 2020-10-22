package ranking_engine;

import common.MapStrConvert;
import common.TextParser;
import common.TextParser.Id_Vector;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.HashMap;

public class RankerMapper extends Mapper<Object, Text, DoubleWritable, Text> {
    private HashMap<String, Integer> word2Id;
    private HashMap<String, Integer> query_vector;

    @Override
    public void setup(Context context) throws IOException {
        Configuration conf = context.getConfiguration();
        query_vector = TextParser.getWords(conf.get(Ranker.StringInput));

        String indexer_output = conf.get(Ranker.StringIEPath);
        Path path_words = new Path(indexer_output, "word_enumeration");
        FileSystem fs = FileSystem.get(conf);
        word2Id = MapStrConvert.hdfsDirStrInt2Map(fs, path_words);
    }

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        Id_Vector doc_vector = new Id_Vector(value);
        double relevance = 0;
        for (String word : query_vector.keySet()) {
            int word_id = word2Id.get(word);
            if (doc_vector.vector.containsKey(word_id)) {
                relevance += query_vector.get(word) * doc_vector.vector.get(word_id);
            }
        }
        value.set(doc_vector.docId.toString());
        context.write(new DoubleWritable(-relevance), value);
        // -relevance to out files with bigger relevance at the top of output.
    }
}
