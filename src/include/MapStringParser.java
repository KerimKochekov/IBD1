package include;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.*;
/*
    This module contains several helpful functions that used few times by lot of classes
    Contains encoding and decoding functions from/to Map, String and Pair
    Includes functions to read/operate files in HDFS
 */
public class MapStringParser {
    private static final String PairSeparator = ";";
    private static final String KVSeparator = "=>";
    private static final ValueCombinator combinator = (v1, v2) -> v2;
    public static final ValueCombinator<Double> sumDouble = Double::sum;
    public static final FromString<Double> parseDouble = Double::parseDouble;
    public static final FromString<Integer> parseInt = Integer::parseInt;
    static final FromString<String> parseString = string -> string;

    public static class Pair<K, V> {
        K key; V value;
        Pair(K key, V value) { this.key = key; this.value = value; }
        public String toString() {
            return key.toString() + KVSeparator + value.toString();
        }
    }
    public interface FromString<T> { T convert(String string);}
    public interface ValueCombinator<T> { T combine(T v1, T v2);}

    static <K, V> Pair<K, V> string2Pair(String string, FromString<K> keyS, FromString<V> valueS, String separator) {
        String[] s = string.split(separator);
        if (s.length != 2)
            return null;
        K key = keyS.convert(s[0]);
        V value = valueS.convert(s[1]);
        return new Pair<K, V>(key, value);
    }
    private static <K, V> HashMap<K, V> PairsToMap(Iterable<String> pairs, FromString<K> keyS, FromString<V> valueS,
                                                        ValueCombinator<V> comb, String separator) {
        HashMap<K, V> map = new HashMap<>();
        for (String pair : pairs) {
            Pair<K, V> obj = string2Pair(pair, keyS, valueS, separator);
            if (obj == null)
                continue;
            if (map.containsKey(obj.key))
                map.put(obj.key, comb.combine(map.get(obj.key), obj.value));
            else
                map.put(obj.key, obj.value);
        }
        return map;
    }
    public static <K, V> HashMap<K, V> stringPairs2Map(Iterable<String> pairs, FromString<K> k2str, FromString<V> v2str, ValueCombinator<V> comb) {
        return PairsToMap(pairs, k2str, v2str, comb, KVSeparator);
    }
    static <K, V> HashMap<K, V> string2Map(String string, FromString<K> keyS, FromString<V> valueS) {
        return PairsToMap(Arrays.asList(string.split(PairSeparator)), keyS, valueS, combinator, KVSeparator);
    }
    //HDFS
    public static ArrayList<String> HDFS2MAP(FileSystem fs, Path dir_path) throws IOException {
        //Read all files in specific path on HDFS and read the contents of files in String format line by line
        RemoteIterator<LocatedFileStatus> it = fs.listFiles(dir_path, false);
        ArrayList<String> pairs = new ArrayList<>();
        while (it.hasNext()) {
            FSDataInputStream inputStream = fs.open(it.next().getPath());
            String in = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            inputStream.close();
            if (in.isEmpty())
                continue;
            String[] lines = in.split("\n");
            pairs.addAll(Arrays.asList(lines));
        }
        return pairs;
    }
    public static HashMap<String, Integer> HDFS_SI2MAP(FileSystem fs, Path dir_path) throws IOException {
        return PairsToMap(HDFS2MAP(fs,dir_path), parseString, parseInt, combinator, "\t");
    }
    public static HashMap<Integer, String> HDFS_IS2MAP(FileSystem fs, Path dir_path) throws IOException {
        return PairsToMap(HDFS2MAP(fs,dir_path), parseInt, parseString, combinator, "\t");
    }
}
