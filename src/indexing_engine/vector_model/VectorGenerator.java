package indexing_engine.vector_model;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;

public class VectorGenerator {
    static final String StringIDF = "indexer.idf";
    static final String StringWords = "indexer.words";

    public static Path run(Path input, Path WordEnumerationPath, Path DocumentCountPath, Path output) throws Exception {
        Configuration conf = new Configuration();
        conf.set("document.ids", WordEnumerationPath.toString());
        conf.set("document.counts", DocumentCountPath.toString());

        Job job = Job.getInstance(conf, "vector_generator");
        job.setJarByClass(VectorGenerator.class);
        job.setMapperClass(VectorGeneratorMapper.class);
        job.setReducerClass(VectorGeneratorReducer.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, input);
        FileOutputFormat.setOutputPath(job, new Path(output, "document_vectors"));

        if (job.waitForCompletion(true))
            return (new Path(output, "document_vectors"));
        throw new Exception("Error with VectorGenerator class");
    }
}
