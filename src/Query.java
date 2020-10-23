import ranking_engine.Ranker;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Query {
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("ERROR: User guide\n" +
                    "$hadoop jar <jar_name>.jar Query <path to output directory of indexer in HDFS> " +
                    "<query text> <number of most relevant docs> \nExample: $hadoop jar project3.jar " +
                    "Query /index \"by the way\" 2");
            return;
        }
        String doc_contents = String.join("\n", Ranker.run(args[0], args[1], Integer.parseInt(args[2])));//saved in directory "query"
        //Save found document contests' title and url in file QUERY(TIME).txt to avoid name collisions and to find easily
        LocalDateTime dateTime = LocalDateTime.now();
        FileWriter fw = new FileWriter("QUERY(" + dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss_dd:MM:yyyy")) + ").txt");
        fw.write("Most relevant " + args[2] + " documents for the query: \"" + args[1] + "\"\n" + doc_contents);
        fw.close();
    }
}
