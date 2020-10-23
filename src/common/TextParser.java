package common;

import common.MapStrConvert.Pair;
import org.apache.hadoop.io.Text;

import java.util.HashMap;

public class TextParser {
    public static String parse(String string) {
        string = string.trim().replaceAll("[^a-zA-Z0-9_\\s]+", "");
        return string.replaceAll("\\s+", " ").toLowerCase();
    }

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

    public static class Id_Text {
        public Integer docId;
        public String text;
        public Id_Text(Text value) {
            Pair<Integer, String> p = MapStrConvert.string2Pair(value.toString(),
                    MapStrConvert.parseInt, MapStrConvert.parseString, "\t");
            docId = p.key;
            text = p.value;
        }
        public HashMap<String, Integer> getWords() {
            return TextParser.getWords(text);
        }
    }

    public static class Id_Vector {
        public Integer docId;
        public HashMap<Integer, Double> vector;
        public Id_Vector(Text value) {
            Pair<Integer, String> p = MapStrConvert.string2Pair(value.toString(),
                    MapStrConvert.parseInt, MapStrConvert.parseString, "\t");
            docId = p.key;
            vector = MapStrConvert.string2Map(p.value, MapStrConvert.parseInt, MapStrConvert.parseDouble);
        }
    }
}
