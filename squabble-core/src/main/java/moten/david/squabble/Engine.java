package moten.david.squabble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import moten.david.util.words.Dictionary;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Sets.SetView;

/**
 * Anagram matching algorithms and immutable functions approach to amendments to
 * Data objects.
 * 
 * @author dave
 */
public class Engine {

    private static Logger log = Logger.getLogger(Engine.class.getName());

    /**
     * Dictionary to check for valid words.
     */
    private final Dictionary dictionary;
    /**
     * Provides the maximum pool of letters (scrabble letters).
     */
    private final Letters letters;

    private final Random random = new Random(System.currentTimeMillis());

    /**
     * Constructor.
     * 
     * @param dictionary
     * @param letters
     */
    public Engine(Dictionary dictionary, Letters letters) {
        this.dictionary = dictionary;
        this.letters = letters;
    }

    /**
     * Returns null if the word cannot be made from a sublist of the list,
     * otherwise returns the list of words that joined and shuffled create the
     * word.
     * 
     * @param list
     * @param word
     * @return
     */
    public static CreateResult createWordFrom(Iterable<Word> list, String word) {
        List<Word> empty = ImmutableList.of();
        // perform a couple of optimizations

        // only include in the list those words that are wholly contained by
        // word.
        List<Word> intersect = Lists.newArrayList();
        Set<String> allLetters = Sets.newHashSet();
        for (Word w : list) {
            if (toList(word).containsAll(toList(w.getWord()))) {
                intersect.add(w);
                allLetters.addAll(toList(w.getWord()));
            }
        }
        // if not all of the word letters turn up in the list then return null
        Set<String> wordLetters = Sets.newHashSet(toList(word));
        SetView<String> complement = Sets.difference(wordLetters, allLetters);
        if (complement.size() > 0)
            return new CreateResult(null, WordStatus.NOT_ANAGRAM);
        else
            return createWordFrom(empty, intersect, word);
    }

    /**
     * Returns a list of the characters in a string.
     * 
     * @param s
     * @return
     */
    private static List<String> toList(String s) {
        List<String> list = Lists.newArrayList();
        for (Character ch : s.toCharArray())
            list.add(ch + "");
        return list;
    }

    /**
     * Wrapper class for result of createWordFrom.
     * 
     * @author dxm
     */
    public static class CreateResult {
        private final Iterable<Word> words;
        private final WordStatus status;

        /**
         * Constructor.
         * 
         * @param words
         * @param status
         */
        public CreateResult(Iterable<Word> words, WordStatus status) {
            super();
            this.words = words;
            this.status = status;
        }

        /**
         * Returns the words that make up the requested word. If status is not
         * WordStatus.OK then returns null.
         * 
         * @return
         */
        public Iterable<Word> getWords() {
            return words;
        }

        /**
         * Returns the status of the words returned.
         * 
         * @return
         */
        public WordStatus getStatus() {
            return status;
        }
    }

    /**
     * Returns null if the word cannot be made from a sublist of the lists used
     * and unused, otherwise returns the list of words from used and unused that
     * joined and shuffled create the word.
     * 
     * @param used
     * @param unused
     * @param word
     * @return
     */
    private static CreateResult createWordFrom(Iterable<Word> used,
            Iterable<Word> unused, final String word) {
        String usedJoined = sort(concatenate(used));
        if (usedJoined.length() > word.length())
            return new CreateResult(null, WordStatus.NOT_ANAGRAM);
        else if (usedJoined.equals(sort(word))) {
            if (matchInHistory(used, word))
                return new CreateResult(null, WordStatus.ROOT_IN_HISTORY);
            else
                return new CreateResult(used, WordStatus.OK);
        } else if (!unused.iterator().hasNext())
            return new CreateResult(used, WordStatus.NOT_ANAGRAM);
        else {
            for (int i = 0; i < Iterables.size(unused); i++) {
                ArrayList<Word> a = Lists.newArrayList(used);
                ArrayList<Word> b = Lists.newArrayList(unused);
                Word part = b.get(i);
                a.add(part);
                b.remove(i);
                CreateResult result = createWordFrom(a, b, word);
                if (result.getStatus().equals(WordStatus.OK))
                    return result;
                a.remove(part);
                result = createWordFrom(a, b, word);
                if (result.getStatus().equals(WordStatus.OK))
                    return result;
            }
            return new CreateResult(null, WordStatus.NOT_ANAGRAM);
        }
    }

    /**
     * Returns true if and only if the candidate uses one of the words in the
     * list as a root word.
     * 
     * @param words
     * @param candidate
     * @return
     */
    private static boolean matchInHistory(Iterable<Word> words, String candidate) {
        for (Word word : words) {
            if (matches(word.getWord(), candidate))
                return true;
            if (word.getMadeFrom() != null)
                for (Word wd : word.getMadeFrom())
                    if (matches(wd.toString(), candidate))
                        return true;
        }
        return false;
    }

    /**
     * Returns true if and only if candidate is built on w as a root.
     * 
     * @param w
     * @param candidate
     * @return
     */
    private static boolean matches(String w, String candidate) {
        Set<String> set = ImmutableSet.of(w, w + "r", w + "s", w + "er", w
                + "es", w + "d", w + "ed", w + "ing", w + "n", "re" + w);
        return set.contains(candidate);
    }

    /**
     * Returns a string being the concatenation of the the words.
     * 
     * @param words
     * @return
     */
    private static String concatenate(Iterable<Word> words) {
        StringBuffer s = new StringBuffer();
        for (Word str : words)
            s.append(str);
        return s.toString();
    }

    /**
     * Sorts a string alphabetically.
     * 
     * @param s
     * @return
     */
    private static String sort(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }

    /**
     * Get a list of all the words currently displayed from Data.
     * 
     * @param data
     * @return
     */
    private List<Word> getCurrentWords(Data data) {
        Builder<Word> builder = ImmutableList.builder();
        if (data.getMap().keySet() != null)
            for (User user : data.getMap().keySet())
                builder.addAll(data.getMap().get(user));
        return builder.build();
    }

    /**
     * Holds the result of a user suggesting a word.
     * 
     * @author dave
     */
    public static class Result {
        private final Data data;
        private final WordStatus status;

        public Data getData() {
            return data;
        }

        public WordStatus getStatus() {
            return status;
        }

        public Result(Data data, WordStatus status) {
            super();
            this.data = data;
            this.status = status;
        }
    }

    /**
     * Status for the word submitted.
     * 
     * @author dxm
     */
    public static enum WordStatus {
        NOT_LONG_ENOUGH, NOT_IN_DICTIONARY, NOT_ANAGRAM, OK, ROOT_IN_HISTORY;
    }

    /**
     * Returns the new Data after a word is submitted by a user.
     * 
     * @param data
     * @param user
     * @param word
     * @return
     */
    public Result wordSubmitted(Data data, User user, String word) {
        if (word.length() < user.getMinimumChars())
            return new Result(data, WordStatus.NOT_LONG_ENOUGH);
        if (!dictionary.isValid(word))
            return new Result(data, WordStatus.NOT_IN_DICTIONARY);
        CreateResult result = createWordFrom(getCurrentWords(data), word);
        if (!result.getStatus().equals(WordStatus.OK))
            return new Result(data, result.getStatus());
        else {
            ImmutableListMultimap<User, Word> map = addWord(data, user, word,
                    Lists.newArrayList(result.getWords()));
            return new Result(new Data(map), WordStatus.OK);
        }
    }

    /**
     * Add a word to user and return the new Data (Data is immutable).
     * 
     * @param data
     * @param user
     * @param word
     * @param parts
     * @return
     */
    private ImmutableListMultimap<User, Word> addWord(Data data, User user,
            String word, List<Word> parts) {
        Word w = new Word(user, word, parts);
        ListMultimap<User, Word> map = ArrayListMultimap.create(data.getMap());
        for (Word part : parts) {
            map.remove(part.getOwner(), part);
        }
        map.put(user, w);
        return ImmutableListMultimap.copyOf(map);
    }

    public Data turnLetter(Data data, User board) {
        log.info("turning letter");
        List<String> used = getUsedLetters(data);
        List<String> available = Lists.newArrayList();
        available.addAll(letters.getLetters());
        for (String ch : used)
            available.remove(ch);
        if (available.size() == 0)
            return data;
        int i = random.nextInt(available.size());
        String nextLetter = available.get(i);

        // add nextLetter to the board user
        ListMultimap<User, Word> map = ArrayListMultimap.create(data.getMap());
        map.put(board, new Word(board, nextLetter));
        log.info("added letter " + nextLetter + " to board");
        return new Data(map);
    }

    /**
     * Returns the list of letters currently used across all user words.
     * 
     * @param data
     * @return
     */
    private List<String> getUsedLetters(Data data) {
        List<String> list = Lists.newArrayList();
        for (User user : data.getMap().keySet())
            for (Word word : data.getMap().get(user)) {
                String s = word.getWord();
                for (Character ch : s.toCharArray()) {
                    list.add(ch + "");
                }
            }
        return list;
    }
}
