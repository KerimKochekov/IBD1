package include;

import java.util.HashMap;
import include.MapStringParser.Pair;
import org.apache.hadoop.io.Text;

public class TextParser {
    //Text parser with regex(removing extra spaces, non-letter and non-digit chars)
    public static String parse(String string) {
        string = string.trim().replaceAll("[^a-zA-Z0-9_\\s]+", "");
        return string.replaceAll("\\s+", " ").toLowerCase();
    }
    //For given text, first parse text to get several strings
    //And later create and return HashMap created from these strings
    public static HashMap<String, Integer> getWords(String string) {
        String[] words = string.split(" ");
        HashMap<String, Integer> map = new HashMap<>();
        for (String word : words) {
            if (map.containsKey(word))
                map.put(word, map.get(word) + 1);
            else
                map.put(word, 1);
        }
        return map;
    }
    //Used in indexing_engine to read from HDFS files and decode it in form (doc_id,text)
    public static class Id_Text {
        public Integer docId;
        public String text;
        public Id_Text(Text value) {
            Pair<Integer, String> p = MapStringParser.string2Pair(value.toString(),
                    MapStringParser.parseInt, MapStringParser.parseString, "\t");
            docId = p.key;
            text = p.value;
        }
        public HashMap<String, Integer> getWords() {
            return TextParser.getWords(text);
        }
    }
    //Used in ranking_engine to handle computed indexer vectors in form (doc_id,vector)
    public static class Id_Vector {
        public Integer docId;
        public HashMap<Integer, Double> vector;
        public Id_Vector(Text value) {
            Pair<Integer, String> p = MapStringParser.string2Pair(value.toString(),
                    MapStringParser.parseInt, MapStringParser.parseString, "\t");
            docId = p.key;
            vector = MapStringParser.string2Map(p.value, MapStringParser.parseInt, MapStringParser.parseDouble);
        }
    }
}
