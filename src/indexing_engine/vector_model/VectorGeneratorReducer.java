package indexing_engine.vector_model;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import include.MapStringParser;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class VectorGeneratorReducer extends Reducer<IntWritable, Text, IntWritable, Text> {
    private static final DecimalFormat df = new DecimalFormat("#.#####"); //used to limit precision in 5 digits
    @Override
    public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        ArrayList<String> pairs = new ArrayList<>();
        for (Text val : values)
            pairs.add(val.toString());
        //For all pairs in specific document, add inverse values of same word_id and transform to new HashMap
        HashMap<Integer, Double> map = MapStringParser.stringPairs2Map(pairs, MapStringParser.parseInt, MapStringParser.parseDouble, MapStringParser.sumDouble);
        SortedSet<Integer> keys = new TreeSet<Integer>(map.keySet());
        pairs = new ArrayList<>();
        for (Integer map_key : keys) {
            double value = map.get(map_key); //limit inverse_values of words in 5 digits
            pairs.add(map_key.toString() + "=>" + df.format(value));
        }
        context.write(key, new Text(String.join(";", pairs)));
    }
}
