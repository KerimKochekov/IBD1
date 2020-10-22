package indexing_engine.vector_model;

import java.io.IOException;
import java.util.HashMap;
import common.MapStrConvert;
import common.TextParser.Id_Text;
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
        EnumerationMap = MapStrConvert.hdfsDirStrInt2Map(fs, new Path(conf.get("document.ids")));
        CountMap = MapStrConvert.hdfsDirStrInt2Map(fs, new Path(conf.get("document.counts")));
    }
    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        Id_Text document_text = new Id_Text(value);
        IntWritable doc_id = new IntWritable(document_text.docId);
        HashMap<String, Integer> document_map = document_text.getWords();
        for (String word : document_map.keySet()) {
            Integer word_id = EnumerationMap.get(word);
            double word_idf = CountMap.get(word).doubleValue();
            Double norm_count = document_map.get(word).doubleValue() / word_idf;
            String pair = MapStrConvert.makeStringPair(word_id, norm_count);
            value.set(pair); context.write(doc_id, value);
        }
    }
}
