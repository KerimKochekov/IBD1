package indexing_engine;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import common.TextParser.Id_Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class CounterMapper extends Mapper<Object, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private Set<String> setOfWords = new HashSet<>();
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        Id_Text document_text = new Id_Text(value);
        Text wordText = new Text();
        String[] words = document_text.text.split(" ");
        setOfWords.addAll(Arrays.asList(words));
        for (String word : setOfWords) {
            wordText.set(word);
            context.write(wordText, one);
        }
    }
}
