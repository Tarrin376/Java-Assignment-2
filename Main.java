package com.codewithmosh;
import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            Question1 q1 = new Question1("C:\\Users\\tarri\\IdeaProjects\\program\\src\\stopwords.txt", "C:\\Users\\tarri\\IdeaProjects\\program\\src\\input.txt");
            ArrayList<String> res = q1.findNonStopWords();
            System.out.println(String.join(" ", res) + "\n");
            int maxSize = Math.min(500, res.size());

            System.out.println("100 WORDS:\n");
            measureSortPerformance(res.subList(0, 100).toArray(new String[100]));
            System.out.println("200 WORDS:\n");
            measureSortPerformance(res.subList(0, 200).toArray(new String[200]));
            System.out.println("" + maxSize + " WORDS:\n");
            measureSortPerformance(res.subList(0, maxSize).toArray(new String[maxSize]));
        }
        catch (FileNotFoundException err) {
            System.out.println(err.getMessage());
        }
    }

    public static void measureSortPerformance(String[] words) {
        long beforeInsertionSort = System.nanoTime();
        InsertionSort sort1 = new InsertionSort(words);
        sort1.insertionSort();
        sort1.printComparisons();

        long insertionSortTime = System.nanoTime() - beforeInsertionSort;
        System.out.println("INSERTION SORT -> Time taken (in nanoseconds: " + insertionSortTime + "\n");

        long beforeMergeSort = System.nanoTime();
        MergeSort sort2 = new MergeSort(words);
        sort2.mergeSort(0, words.length - 1);
        sort2.printComparisons();

        long mergeSortTime = System.nanoTime() - beforeMergeSort;
        System.out.println("MERGE SORT -> Time taken (in nanoseconds): " + mergeSortTime + "\n");
    }
}

class Question1 {
    private ArrayList<String> stopWords;
    public ArrayList<String> inputWords;

    public Question1(String stopWordsPath, String inputPath) throws FileNotFoundException {
        stopWords = new ArrayList<>();
        readWords(stopWordsPath, true);
        readWords(inputPath, false);
    }

    private void readWords(String stopWordsPath, boolean areStopWords) throws FileNotFoundException {
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
            throw new FileNotFoundException("File was not found");
        }
    }

    public ArrayList<String> findNonStopWords() {
        ArrayList<String> nonStopWords = new ArrayList<>();
        Trie trie = new Trie();
        trie.insertWords(stopWords);

        for (String word : inputWords) {
            Trie.TrieNode tmp = trie.root;
            int N = word.length();
            boolean valid = true;

            for (int i = 0; i < N; i++) {
                char cur = word.charAt(i);
                int index = Character.isLetter(cur) ? Character.toUpperCase(cur) - 33 : cur - 33;
                if (!Character.isLetter(cur)) System.out.println("Cur: " + cur + " Index: " + index);

                if ("{|}~".contains("" + cur)) index -= 26;
                if (index > 66 || tmp.children[index] == null) break;

                if (tmp.children[index].isWord && i == N - 1) {
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
    class TrieNode {
        public TrieNode[] children;
        public boolean isWord;

        public TrieNode() {
            this.children = new TrieNode[67];
            this.isWord = false;
        }
    }

    public TrieNode root;

    public Trie() {
        root = new TrieNode();
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

interface ISort {
    void printComparisons();
}

class MergeSort implements ISort {
    private int comparisons;
    public String[] words;

    public MergeSort(String[] words) {
        this.words = words;
    }

    public String[] mergeSort(int low, int high) {
        if (low == high) {
            return new String[] {words[low]};
        }

        int mid = low + ((high - low) / 2);
        String[] left = mergeSort(low, mid);
        String[] right = mergeSort(mid + 1, high);

        return merge(left, right);
    }

    public String[] merge(String[] left, String[] right) {
        int left_index = 0, right_index = 0;
        int N = left.length, M = right.length;
        String[] merged = new String[N + M];

        for (int i = 0; i < N + M; i++) {
            if (right_index == M || (left_index < N && left[left_index].toLowerCase().compareTo(right[right_index]) <= 0)) {
                merged[i] = left[left_index];
                left_index++;
            } else {
                merged[i] = right[right_index];
                right_index++;
            }

            comparisons++;
        }

        return merged;
    }

    public void printComparisons() {
        System.out.println("MERGE SORT -> Comparisons: " + comparisons);
    }
}

class InsertionSort implements ISort {
    private int comparisons;
    public String[] words;

    public InsertionSort(String[] words) {
        this.words = words;
    }

    public String[] insertionSort() {
        for (int i = 1; i < words.length; i++) {
            String cur = words[i];
            int index = i;

            while (index > 0 && words[index - 1].toLowerCase().compareTo(cur.toLowerCase()) > 0) {
                words[index] = words[index - 1];
                index--;
                comparisons++;
            }

            words[index] = cur;
        }

        return words;
    }

    public void printComparisons() {
        System.out.println("INSERTION SORT -> Comparisons: " + comparisons);
    }
}