package com.codewithmosh;
import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            Question1 q1 = new Question1("C:\\Users\\Tarrin\\IdeaProjects\\javaProject\\src\\stopwords.txt", "C:\\Users\\Tarrin\\IdeaProjects\\javaProject\\src\\input.txt");
            ArrayList<String> res = q1.findNonStopWords();
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

    /*
    This method is responsible for measuring the execution time of a sorting algorithm that
    implements the 'ISort' interface.
     */
    public static <T extends ISort> void measureSortPerformance(String[] words, T obj) {
        long start = System.nanoTime();
        obj.sort(words);
        long time = System.nanoTime() - start;

        System.out.println(obj + " Comparisons -> " + obj.getComparisons());
        System.out.println(obj + " Time taken (in nanoseconds) -> " + time + "\n");
    }
}

class Question1 {
    // The set of all stop words found in the "stopwords.txt" file.
    private final Set<String> stopWords;
    // The list of all input words found in the "input.txt" file
    public List<String> inputWords;

    public Question1(String stopWordsPath, String inputPath) throws FileNotFoundException {
        stopWords = new HashSet<>();
        inputWords = new ArrayList<>();
        readWords(stopWordsPath, stopWords);
        readWords(inputPath, inputWords);
    }

    /*
    This method is responsible for reading words from a given .txt file
    and adds each of these words to the passed in data structure that must
    implement the Collection<String> interface.
     */
    private <T extends Collection<String>> void readWords(String stopWordsPath, T obj) throws FileNotFoundException {
        try {
            File file = new File(stopWordsPath);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String next = scanner.next();
                // Checks if it is adding to the "stopWords" set
                if (obj instanceof HashSet) obj.add(next.toLowerCase());
                else obj.add(next);
            }

            scanner.close();
        } catch (FileNotFoundException err) {
            throw new FileNotFoundException("File was not found");
        }
    }

    /*
    This method holds logic for finding all the stop words in the "inputWords" array list.
    It handles cases such as an input word being an acronym e.g. "IT" is not the same as "it".
    If an input word is in the "stopWords" set, only the punctuation is added to the result but
    if it is not in the "stopWords" set, the whole word is added instead.

    Returns:
        nonStopWords -> The resulting list after removing all stop words.
     */
    public ArrayList<String> findNonStopWords() {
        ArrayList<String> nonStopWords = new ArrayList<>();

        for (String word : inputWords) {
            int N = word.length();
            int end = N - 1;
            int start = 0;

            // Two-pointer technique that strips the punctuation from the left and right of the word
            while (start <= end) {
                if (!Character.isLetter(word.charAt(end)) && !Character.isDigit(word.charAt(end))) end--;
                else if (!Character.isLetter(word.charAt(start)) && !Character.isDigit(word.charAt(start))) start++;
                else break;
            }

            // Checks if the word just contains punctuation
            if (start > end) {
                nonStopWords.add(word);
                continue;
            }

            String str = word.substring(start, end + 1);
            if (!stopWords.contains(str.toLowerCase()) || (str.equals(str.toUpperCase()) && str.length() > 1 && !stopWords.contains(str))) {
                nonStopWords.add(word);
                continue;
            }

            String punctuation = word.substring(0, start) + word.substring(end + 1, N);
            if (punctuation.length() > 0) {
                nonStopWords.add(punctuation);
            }
        }

        return nonStopWords;
    }
}

/*
Interface that is implemented by both the "InsertionSort" and
"MergeSort" class to allow for re-usability when measuring
the performance of the two algorithms.
 */
interface ISort {
    // Returns the number of comparisons performed in the most recent sort.
    int getComparisons();
    // Resets the 'comparisons' variable when called and executes the sorting algorithm
    // and returns the result.
    String[] sort(String[] words);
    // Returns the name of the algorithm.
    String toString();
}

class MergeSort implements ISort {
    // Holds the number of comparisons made after the execution of the sorting algorithm.
    private int comparisons;
    // Contains the list of words that are to be sorted.
    private String[] words;

    public String[] sort(String[] words) {
        this.words = words;
        comparisons = 0;

        if (words.length > 0) return mergeSort(0, words.length - 1);
        else return words;
    }

    /*
    This method is responsible for dividing the array into two parts
    and calling the 'merge' method to obtain the sorted merged array.

    Returns:
        The sorted merged result of the two arrays.
     */
    public String[] mergeSort(int low, int high) {
        if (low == high) {
            return new String[] {words[low]};
        }

        int mid = low + ((high - low) / 2);
        String[] left = mergeSort(low, mid);
        String[] right = mergeSort(mid + 1, high);

        return merge(left, right);
    }

    /*
    This method contains the logic of merging the 'left' and 'right'
    sorted arrays into a single sorted array. It uses the two-pointer
    technique and is case-insensitive.

    Returns:
        merged -> The resulting string array after merging the 'left' and 'right' arrays.
     */
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
    // Holds the number of comparisons made after the execution of the sorting algorithm.
    private int comparisons;
    // Contains the list of words that are to be sorted.
    private String[] words;

    public String[] sort(String[] words) {
        this.words = words;
        comparisons = 0;
        return insertionSort();
    }

    /*
    This method runs the insertion sort algorithm and keeps track of
    how many comparisons (swaps) were made to get to the final result.

    Returns:
        words -> The original array passed into the constructor but instead sorted.
     */
    public String[] insertionSort() {
        for (int i = 1; i < words.length; i++) {
            String cur = words[i];
            int index = i;

            while (index > 0 && cur.compareToIgnoreCase(words[index - 1]) < 0) {
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