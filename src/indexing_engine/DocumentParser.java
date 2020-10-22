package indexing_engine;

import common.TextParser;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class DocumentParser {
    public static class TextMapper extends Mapper<Object, Text, IntWritable, Text> {
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            try {
                JSONObject jb = new JSONObject(value.toString());
                int id = jb.getInt("id");
                String text = TextParser.parse(jb.getString("text"));
                context.write(new IntWritable(id), new Text(text));
            } catch (JSONException e) {
                value.set("JSON Error");
                context.write(new IntWritable(-1), value);
            }
        }
    }

    public static class TitleUrlMapper extends Mapper<Object, Text, IntWritable, Text> {
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            try {
                JSONObject jb = new JSONObject(value.toString());
                int id = jb.getInt("id");
                String title = jb.getString("title");
                String url = jb.getString("url");
                context.write(new IntWritable(id), new Text(title + " " + url));
            } catch (JSONException e) {
                value.set("JSON Error");
                context.write(new IntWritable(-1), value);
            }
        }
    }

    public static class DocumentReducer extends Reducer<IntWritable, Text, IntWritable, Text> {
        public void reduce(IntWritable key, Iterable<Text> texts, Context context)
                throws IOException, InterruptedException {
            StringBuilder outputText = new StringBuilder();
            for (Text text : texts)
                outputText.append(text.toString());
            context.write(key, new Text(outputText.toString()));
        }
    }

    public static Path run(Path inputPath, Path outputDir, int parseMode) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "document_parser");
        job.setJarByClass(DocumentParser.class);
        FileSystem fs = FileSystem.get(conf);
        FileStatus[] fs_ls = fs.listStatus(inputPath);
        Path outputPath;

        if (parseMode == 0) {
            outputPath = new Path(outputDir, "texts");
            for (FileStatus fsi : fs_ls)
                MultipleInputs.addInputPath(job, fsi.getPath(), TextInputFormat.class, TextMapper.class);
        } else if (parseMode == 1) {
            outputPath = new Path(outputDir, "title_urls");
            for (FileStatus fsi : fs_ls)
                MultipleInputs.addInputPath(job, fsi.getPath(), TextInputFormat.class, TitleUrlMapper.class);
        } else
            throw new Exception("ParseMode not found");

        FileOutputFormat.setOutputPath(job, outputPath);
        job.setReducerClass(DocumentReducer.class);
        job.setNumReduceTasks(1);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);

        if (job.waitForCompletion(true))
            return outputPath;
        throw new Exception("Error with DocumentParser class");
    }
}
