import java.util.*;
import java.io.*;

public class Section1 {
    public static void main(String[] args) {
        Question1 q1 = new Question1("stopwords.txt", "input.txt");
        ArrayList<String> res = q1.findNonStopWords();
        int maxSize = Math.min(500, res.size());

        measureSortPerformance(res.subList(0, 100).toArray(new String[100]));
        measureSortPerformance(res.subList(0, 200).toArray(new String[200]));
        measureSortPerformance(res.subList(0, maxSize).toArray(new String[maxSize]));
    }

    public static void measureSortPerformance(String[] words) {
        long beforeInsertionSort = System.nanoTime();
        insertionSort(words);
        long insertionSortTime = System.nanoTime() - beforeInsertionSort;

        long beforeMergeSort = System.nanoTime();
        mergeSort(words, 0, words.length - 1);
        long mergeSortTime = System.nanoTime() - beforeMergeSort;

        System.out.println("MERGE SORT -> Time taken (in nanoseconds): " + mergeSortTime + " | Words: " + words.length);
        System.out.println("INSERTION SORT -> Time taken (in nanoseconds): " + insertionSortTime + " | Words: " + words.length + "\n");
    }

    private static String[] insertionSort(String[] listOfWords) {
        for (int i = 1; i < listOfWords.length; i++) {
            String cur = listOfWords[i];
            int index = i;

            while (index > 0 && listOfWords[index - 1].toLowerCase().compareTo(cur.toLowerCase()) > 0) {
                listOfWords[index] = listOfWords[index - 1];
                index--;
            }

            listOfWords[index] = cur;
        }

        return listOfWords;
    }

    private static String[] mergeSort(String[] listOfWords, int low, int high) {
        if (low == high) {
            return new String[] {listOfWords[low]};
        }

        int mid = low + ((high - low) / 2);
        String[] left = mergeSort(listOfWords, low, mid);
        String[] right = mergeSort(listOfWords, mid + 1, high);

        return merge(left, right);
    }

    private static String[] merge(String[] left, String[] right) {
        int li = 0, ri = 0;
        int N = left.length, M = right.length;
        String[] merged = new String[N + M];

        for (int i = 0; i < N + M; i++) {
            if (ri == M || (li < N && left[li].toLowerCase().compareTo(right[ri]) <= 0)) {
                merged[i] = left[li];
                li++;
            } else {
                merged[i] = right[ri];
                ri++;
            }
        }

        return merged;
    }
}

class Question1 {
    private ArrayList<String> stopWords;
    public ArrayList<String> inputWords;

    public Question1(String stopWordsPath, String inputPath) {
        stopWords = new ArrayList<>();
        readWords(stopWordsPath, true);
        readWords(inputPath, false);
    }

    private void readWords(String stopWordsPath, boolean areStopWords) {
        try {
            File file = new File(stopWordsPath);
            Scanner scanner = new Scanner(file);
            ArrayList<String> words = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String next = scanner.next();
                words.add(next);
            }

            if (areStopWords) this.stopWords = words;
            else this.inputWords = words;
            scanner.close();
        }
        catch (FileNotFoundException err) {
            System.out.println("File was not found");
        }
    }

    public ArrayList<String> findNonStopWords() {
        ArrayList<String> nonStopWords = new ArrayList<>();
        Trie trieDS = new Trie();
        trieDS.insertWords(stopWords);

        for (String word : inputWords) {
            Trie.TrieNode tmp = trieDS.root;
            int N = word.length();
            boolean valid = true;
            
            for (int i = 0; i < N; i++) {
                char cur = word.charAt(i);
                int index = Character.isLetter(cur) ? Character.toUpperCase(cur) - 33 : cur - 33;

                if ("{|}~".contains("" + cur)) index -= 26;
                if (index > 66 || tmp.children[index] == null) break;

                if (tmp.children[index].isWord && (i == N - 1 || (i == N - 2 && !Character.isLetter(word.charAt(i + 1))))) {
                    valid = false;
                    break;
                }
                else {
                    tmp = tmp.children[index];
                }
            }

            if (valid) {
                nonStopWords.add(word);
            }
        }

        return nonStopWords;
    }
}

class Trie {
    public TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    class TrieNode {
        public TrieNode[] children;
        public boolean isWord;

        public TrieNode() {
            this.children = new TrieNode[67];
            this.isWord = false;
        }
    }

    public void insertWords(ArrayList<String> stopWords) {
        for (String word : stopWords) {
            TrieNode tmp = root;
            int N = word.length();

            for (int i = 0; i < N; i++) {
                char cur = word.charAt(i);
                int index = Character.isLetter(cur) ? Character.toUpperCase(cur) - 33 : cur - 33;

                if ("{|}~".contains("" + cur)) 
                    index -= 26;

                if (tmp.children[index] == null) tmp.children[index] = new TrieNode();
                if (i == N - 1) tmp.children[index].isWord = true;
                tmp = tmp.children[index];
            }
        }
    }
}
