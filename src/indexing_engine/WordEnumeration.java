package indexing_engine;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;

public class WordEnumeration {
    public static class EnumerationReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private static int id = 0;
        public void reduce(Text word, Iterable<IntWritable> values, Context context)
        throws IOException, InterruptedException { context.write(word,new IntWritable(++id)); }
    }
    public static Path run(Path inputPath, Path outputDir) throws Exception {
        Job job = Job.getInstance(new Configuration(), "word_enumeration");
        job.setJarByClass(WordEnumeration.class);
        job.setMapperClass(CounterMapper.class);
        job.setCombinerClass(EnumerationReducer.class);
        job.setReducerClass(EnumerationReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setNumReduceTasks(1);

        FileInputFormat.addInputPath(job, inputPath);
        Path outputPath = new Path(outputDir, "word_enumeration");
        FileOutputFormat.setOutputPath(job, outputPath);
        if (job.waitForCompletion(true))
            return outputPath;
        throw new Exception("Error with WordEnumeration class");
    }
}
