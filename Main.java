package com.codewithmosh;
import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            Question1 q1 = new Question1("C:\\Users\\Tarrin\\IdeaProjects\\javaProject\\src\\stopwords.txt", "C:\\Users\\Tarrin\\IdeaProjects\\javaProject\\src\\input.txt");
            ArrayList<String> res = q1.findNonStopWords();
            System.out.println(String.join(" ", res) + "\n");

            int maxSize = Math.min(500, res.size());
            MergeSort mergeSort = new MergeSort();
            InsertionSort insertionSort = new InsertionSort();

            System.out.println("100 WORDS:\n");
            measureSortPerformance(res.subList(0, 100).toArray(new String[100]), mergeSort);
            measureSortPerformance(res.subList(0, 100).toArray(new String[100]), insertionSort);

            System.out.println("200 WORDS:\n");
            measureSortPerformance(res.subList(0, 200).toArray(new String[200]), mergeSort);
            measureSortPerformance(res.subList(0, 200).toArray(new String[200]), insertionSort);

            System.out.println("" + maxSize + " WORDS:\n");
            measureSortPerformance(res.subList(0, maxSize).toArray(new String[maxSize]), mergeSort);
            measureSortPerformance(res.subList(0, maxSize).toArray(new String[maxSize]), insertionSort);
        }
        catch (FileNotFoundException err) {
            System.out.println(err.getMessage());
        }
    }

    public static <T extends ISort> void measureSortPerformance(String[] words, T obj) {
        long start = System.nanoTime();
        obj.sort(words);
        long time = System.nanoTime() - start;

        System.out.println(obj + " Comparisons -> " + obj.getComparisons());
        System.out.println(obj + " Time taken (in nanoseconds: " + time + "\n");
    }
}

class Question1 {
    private final Set<String> stopWords;
    public List<String> inputWords;

    public Question1(String stopWordsPath, String inputPath) throws FileNotFoundException {
        stopWords = new HashSet<>();
        inputWords = new ArrayList<>();
        readWords(stopWordsPath, stopWords);
        readWords(inputPath, inputWords);
    }

    private <T extends Collection<String>> void readWords(String stopWordsPath, T words) throws FileNotFoundException {
        try {
            File file = new File(stopWordsPath);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String next = scanner.next();
                words.add(next);
            }

            scanner.close();
        } catch (FileNotFoundException err) {
            throw new FileNotFoundException("File was not found");
        }
    }

    public ArrayList<String> findNonStopWords() {
        ArrayList<String> nonStopWords = new ArrayList<>();

        for (String word : inputWords) {
            int N = word.length();
            int index = N - 1;

            while (!Character.isLetter(word.charAt(index)) && !Character.isDigit(word.charAt(index))) {
                index--;
            }

            String str = word.substring(0, index + 1).toLowerCase();
            if (!stopWords.contains(str)) {
                nonStopWords.add(word);
                continue;
            }

            if (index < N - 1) {
                nonStopWords.add(word.substring(index + 1, N));
            }
        }

        return nonStopWords;
    }
}

interface ISort {
    int getComparisons();
    String[] sort(String[] words);
    String toString();
}

class MergeSort implements ISort {
    private int comparisons;
    private String[] words;

    public String[] sort(String[] words) {
        this.words = words;
        return mergeSort(0, words.length - 1);
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
            if (right_index == M || (left_index < N && left[left_index].compareToIgnoreCase(right[right_index]) <= 0)) {
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

    public int getComparisons() {
        return comparisons;
    }

    @Override
    public String toString() {
        return "MERGE SORT";
    }
}

class InsertionSort implements ISort {
    private int comparisons;
    private String[] words;

    public String[] sort(String[] words) {
        this.words = words;
        return insertionSort();
    }

    public String[] insertionSort() {
        for (int i = 1; i < words.length; i++) {
            String cur = words[i];
            int index = i;

            while (index > 0 && words[index - 1].compareToIgnoreCase(cur) > 0) {
                words[index] = words[index - 1];
                index--;
                comparisons++;
            }

            words[index] = cur;
        }

        return words;
    }

    public int getComparisons() {
        return comparisons;
    }

    @Override
    public String toString() {
        return "INSERTION SORT";
    }
}