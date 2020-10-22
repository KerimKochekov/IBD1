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
                    "\nExample: $hadoop jar project3.jar Indexer /EnWikiSmall /doc_index");
            return;
        }
        FileSystem fs = FileSystem.get(new Configuration());
        Path input = new Path(args[0]);
        Path output = new Path(args[1]);
        if (fs.exists(output))
            fs.delete(output, true);
        fs.close();
        Path parsedPath = DocumentParser.run(input, output, 0); //texts
        DocumentParser.run(input, output, 1); //title_urls
        Path WordEnumerationPath = WordEnumeration.run(parsedPath, output); //word_enumeration
        Path DocumentCountPath = DocumentCount.run(parsedPath, output); //document_count
        VectorGenerator.run(parsedPath, WordEnumerationPath, DocumentCountPath, output); //document_vectors
    }
}
