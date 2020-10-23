package indexing_engine.vector_model;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

import common.MapStrConvert;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class VectorGeneratorReducer extends Reducer<IntWritable, Text, IntWritable, Text> {
    private static DecimalFormat df = new DecimalFormat("#.#####");
    @Override
    public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        ArrayList<String> pairs = new ArrayList<>();
        for (Text val : values)
            pairs.add(val.toString());
        HashMap<Integer, Double> map = MapStrConvert.stringPairs2Map(pairs, MapStrConvert.parseInt, MapStrConvert.parseDouble, MapStrConvert.sumDouble);
        SortedSet<Integer> keys = new TreeSet<Integer>(map.keySet());
        pairs = new ArrayList<>();
        for (Integer map_key : keys) {
            double value = map.get(map_key);
            pairs.add(map_key.toString() + "=>" + df.format(value));
        }
        context.write(key, new Text(String.join(";", pairs)));
    }
}
