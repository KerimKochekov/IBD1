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

public class DocumentCount {
    public static class IDFReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text word, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int occurrence = 0;
            for (IntWritable val : values)
                occurrence += val.get();
            IntWritable result = new IntWritable();
            result.set(occurrence);
            context.write(word, result);
        }
    }
    public static Path run(Path inputPath, Path outputDir) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "document_count");
        job.setJarByClass(DocumentCount.class);
        job.setMapperClass(CounterMapper.class);
        job.setCombinerClass(IDFReducer.class);
        job.setReducerClass(IDFReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, inputPath);
        Path outputPath = new Path(outputDir, "document_count");
        FileOutputFormat.setOutputPath(job, outputPath);
        if (job.waitForCompletion(true))
            return outputPath;
        throw new Exception("Error with DocumentCount class");
    }
}
