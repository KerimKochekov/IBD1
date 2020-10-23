import indexing_engine.DocumentParser;
import indexing_engine.DocumentCount;
import indexing_engine.vector_model.VectorGenerator;
import indexing_engine.WordEnumeration;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class Indexer {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("ERROR: User guide\n" +
                    "$hadoop jar <jar_name>.jar Indexer <path to input directory in HDFS> <path to output directory in HDFS>" +
                    "\nExample: $hadoop jar project3.jar Indexer /data /indexer");
            return;
        }
        FileSystem fs = FileSystem.get(new Configuration());
        Path input = new Path(args[0]);
        Path output = new Path(args[1]);
        if (fs.exists(output)) //If output file exists, then delete it from HDFS
            fs.delete(output, true);
        fs.close();
        Path parsedPath = DocumentParser.run(input, output, 0); //saved in directory "texts"
        DocumentParser.run(input, output, 1); //saved in directory "title_urls"
        Path WordEnumerationPath = WordEnumeration.run(parsedPath, output); //saved in directory "word_enumeration"
        Path DocumentCountPath = DocumentCount.run(parsedPath, output); //saved in directory "document_count"
        VectorGenerator.run(parsedPath, WordEnumerationPath, DocumentCountPath, output); //saved in directory "document_vectors"
    }
}
