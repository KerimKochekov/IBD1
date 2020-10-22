package ranking_engine;

import common.MapStrConvert;
import common.TextParser;
import indexing_engine.vector_model.VectorGenerator;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class Ranker {
    private static final String JobName = "query";
    static final String StringIEPath = "query.ei_path";
    static final String StringInput = "query.input";
    private static final String OutputDir = "query";
    static final String OutputDocSeparator = "\\|";

    public static ArrayList<String> run(String indexer_output, String query, int doc_number) throws Exception {
        // Setup configuration
        String parsedQuery = TextParser.parse(query);
        Path outputDir = new Path(indexer_output, OutputDir);
        Configuration conf = new Configuration();
        // Add words and idf to conf
        conf.set(StringIEPath, indexer_output);
        conf.set(StringInput, parsedQuery);
        // Check output dir
        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(outputDir)) {
            fs.delete(outputDir, true);
        }
        // Make job
        Job job = Job.getInstance(conf, JobName);
        job.setJarByClass(Ranker.class);
        job.setMapperClass(RankerMapper.class);
        job.setReducerClass(RankerReducer.class);
        job.setOutputKeyClass(DoubleWritable.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(indexer_output, "document_vectors"));
        FileOutputFormat.setOutputPath(job, outputDir);
        if (!job.waitForCompletion(true)) {
            throw new Exception("Ranker.run() was not completed");
        }
        // Success, do output
        conf = job.getConfiguration();
        fs = FileSystem.get(conf);
        // Read docId -> title URL
        Path path_titles = new Path(indexer_output, "title_urls");
        HashMap<Integer, String> docId2TitleUrl = MapStrConvert.hdfsDirIntStr2Map(fs, path_titles);
        // Read output
        ArrayList<Output> outs = readMapRedOutput(fs, outputDir);
        // Remove output dir
        fs.delete(outputDir, true);
        fs.close();
        // Extract most relevant doc ids and add their title and url
        ArrayList<String> docs = new ArrayList<>();
        for (Output o : outs) {
            for (String docId : o.docs) {
                int id = Integer.parseInt(docId);
                docs.add(docId2TitleUrl.get(id));
                doc_number--;
                if (doc_number == 0) break;
            }
            if (doc_number == 0) break;
        }
        return docs;
    }

    public static class Output {
        double relevance;
        String[] docs;

        Output(String line) {
            String[] rel_docs = line.split(MapStrConvert.FileKVSeparator);
            docs = rel_docs[1].split(OutputDocSeparator);
            relevance = Double.parseDouble(rel_docs[0]);
        }
    }

    private static final Comparator<Output> compare = Comparator.comparingDouble(v -> v.relevance);

    private static ArrayList<Output> readMapRedOutput(FileSystem fs, Path path) throws IOException {
        ArrayList<Output> outs = new ArrayList<>();
        RemoteIterator<LocatedFileStatus> it;
        it = fs.listFiles(path, false);

        while (it.hasNext()) {
            FSDataInputStream inputStream = fs.open(it.next().getPath());
            String in = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            inputStream.close();
            if (in.isEmpty()) continue;
            String[] lines = in.split(MapStrConvert.FilePairSeparator);
            for (String line : lines) {
                outs.add(new Output(line));
            }
        }
        outs.sort(compare);
        return outs;
    }
}
