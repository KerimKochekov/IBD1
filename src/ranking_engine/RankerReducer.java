package ranking_engine;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;

public class RankerReducer extends Reducer<DoubleWritable, Text, DoubleWritable, Text> {
    @Override
    public void reduce(DoubleWritable key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        ArrayList<String> docs = new ArrayList<>();
        for (Text val : values) {
            docs.add(val.toString());
        }
        context.write(key, new Text(String.join(Ranker.OutputDocSeparator, docs)));
    }
}
