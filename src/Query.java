import ranking_engine.Ranker;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Query {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage:\n$hadoop jar <jar_name>.jar Query " +
                    "<path to output directory of Indexer on HDFS> " +
                    "<query in quotes> <number of most relevant docs>" +
                    "\nExample:\n$hadoop jar ibdhmw.jar Query " +
                    "/indexed_docs \"what is big data\" 3");

            return;
        }
        // Get arguments
        String indexer_output = args[0];
        String query = args[1];
        int doc_number;
        try {
            doc_number = Integer.parseInt(args[2]);
        } catch (Exception e) {
            System.out.println("Number of most relevant docs is incorrect");
            return;
        }
        // Run ranker
        ArrayList<String> docs = Ranker.run(indexer_output, query, doc_number);
        // Join docs
        String docs_inline = String.join("\n", docs);
        // Print output
        System.out.println(docs_inline);
        // Write output
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        FileWriter fw = new FileWriter("query_" + dateTime.format(formatter) + ".txt");
        fw.write("Result of the query \"" + query + "\"\n\n" + docs_inline + "\n");
        fw.close();
    }
}
