import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Spelling Bee
 * This program accepts an input of letters. It prints to an output file
 * all English words that can be generated from those letters.
 * For example: if the user inputs the letters "doggo" the program will generate:
 * do
 * dog
 * doggo
 * go
 * god
 * gog
 * gogo
 * goo
 * good
 * It utilizes recursion to generate the strings, mergesort to sort them, and
 * binary search to find them in a dictionary.
 *
 * @author Zach Blick, Kai Mawakana
 * Written on March 5, 2023 for CS2 @ Menlo School
 *
 * DO NOT MODIFY MAIN OR ANY OF THE METHOD HEADERS.
 */
public class SpellingBee {

    private String letters;
    private ArrayList<String> words;
    public static final int DICTIONARY_SIZE = 143091;
    public static final String[] DICTIONARY = new String[DICTIONARY_SIZE];

    public SpellingBee(String letters) {
        this.letters = letters;
        words = new ArrayList<>();
    }

    // TODO: generate all possible substrings and permutations of the letters.
    //  Store them all in the ArrayList words. Do this by calling ANOTHER method
    //  that will find the substrings recursively.
    public void generate()
    {
        generateHelper("", letters);
    }

    private void generateHelper(String prefix, String remaining) {
        // Add the current prefix if it's not empty
        if (!prefix.isEmpty()) {
            words.add(prefix);
        }

        // Try adding each remaining character to the prefix
        for (int i = 0; i < remaining.length(); i++) {
            // Skip duplicates in the remaining string
            if (i > 0 && remaining.charAt(i) == remaining.charAt(i - 1))
            {
                continue;
            }

            generateHelper(prefix + remaining.charAt(i),
                    remaining.substring(0, i) + remaining.substring(i + 1));
        }
    }

    // TODO: Apply mergesort to sort all words. Do this by calling ANOTHER method
    //  that will find the substrings recursively.
    public void sort() {
        words = mergeSort(words);
    }

    private ArrayList<String> mergeSort(ArrayList<String> list)
    {
        // Separation is complete / list is very small
        if (list.size() <= 1)
        {
            return list;
        }

        // Split list in half
        int mid = list.size() / 2;
        ArrayList<String> left = new ArrayList<>(list.subList(0, mid));
        ArrayList<String> right = new ArrayList<>(list.subList(mid, list.size()));

        left = mergeSort(left);
        right = mergeSort(right);

        return merge(left, right);
    }

    private ArrayList<String> merge(ArrayList<String> left, ArrayList<String> right)
    {
        ArrayList<String> merged = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size())
        {
            if(left.get(i).compareTo(right.get(j)) <= 0)
            {
                merged.add(left.get(i));
                i++;
            }

            else
            {
                merged.add(right.get(j));
                j++;
            }
        }

        while (i < left.size())
        {
            merged.add(left.get(i));
            i++;
        }

        while (j < right.size())
        {
            merged.add(right.get(j));
            j++;
        }

        return merged;

    }

    // Removes duplicates from the sorted list.
    public void removeDuplicates() {
        int i = 0;
        while (i < words.size() - 1) {
            String word = words.get(i);
            if (word.equals(words.get(i + 1)))
                words.remove(i + 1);
            else
                i++;
        }
    }


    // Removes non-words from list using binary search
    public void checkWords() {
        ArrayList<String> validWords = new ArrayList<>();
        for (String word : words)
        {
            if(binarySearch(word))
            {
                validWords.add(word);
            }
        }

        words = validWords;
    }

    private boolean binarySearch(String word)
    {
        int left = 0, right = DICTIONARY_SIZE - 1;

        while (left <= right)
        {
            int mid = left + (right - left) / 2;
            int comparison = DICTIONARY[mid].compareTo(word);

            if (comparison == 0)
            {
                return true;
            }

            else if (comparison < 0)
            {
                left = mid + 1;
            }

            else
            {
                right = mid - 1;
            }
        }

        return false;
    }

    // Returns all valid words
    public void printWords() throws IOException {
        File wordFile = new File("Resources/wordList.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(wordFile, false));
        for (String word : words) {
            System.out.println(word + "\n");
            writer.append(word);
            writer.newLine();
        }
        writer.close();
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public void setWords(ArrayList<String> words) {
        this.words = words;
    }

    public SpellingBee getBee() {
        return this;
    }

    public static void loadDictionary() {
        Scanner s;
        File dictionaryFile = new File("Resources/dictionary.txt");
        try {
            s = new Scanner(dictionaryFile);
        } catch (FileNotFoundException e) {
            System.out.println("Could not open dictionary file.");
            return;
        }
        int i = 0;
        while(s.hasNextLine()) {
            DICTIONARY[i++] = s.nextLine();
        }
    }

    public static void main(String[] args) {

        // Prompt for letters until given only letters.
        Scanner s = new Scanner(System.in);
        String letters;
        do {
            System.out.print("Enter your letters: ");
            letters = s.nextLine();
        }
        while (!letters.matches("[a-zA-Z]+"));

        // Load the dictionary
        SpellingBee.loadDictionary();

        // Generate and print all valid words from those letters.
        SpellingBee sb = new SpellingBee(letters);
        sb.generate();
        sb.sort();
        sb.removeDuplicates();
        sb.checkWords();
        try {
            sb.printWords();
        } catch (IOException e) {
            System.out.println("Could not write to output file.");
        }
        s.close();
    }
}
