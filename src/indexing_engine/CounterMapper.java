package indexing_engine;

import java.io.IOException;
import include.TextParser.Id_Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;

public class CounterMapper extends Mapper<Object, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        Id_Text document_text = new Id_Text(value); //parse value as doc_id and content of doc
        Text wordText = new Text();
        String[] words = document_text.text.split(" "); //separate text to several words
        for (String word : words) {
            wordText.set(word);
            context.write(wordText, one);
        }
    }
}
