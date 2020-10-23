import ranking_engine.Ranker;

import java.io.FileWriter;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class Query {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("ERROR: User guide\n" +
                    "$hadoop jar <jar_name>.jar Query <path to output directory of indexer in HDFS> " +
                    "<query text> <number of most relevant docs> \nExample: $hadoop jar project3.jar " +
                    "Query /index \"by the way\" 2");
            return;
        }
        String doc_contents = String.join("\n", Ranker.run(args[0], args[1], Integer.parseInt(args[2])));//query
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        FileWriter fw = new FileWriter("query_" + dateTime.format(formatter) + ".txt");
        fw.write("Most relevant " + args[2] + " documents for the query: \"" + args[1] + "\"" + doc_contents);
        fw.close();
    }
}
