package moten.david.squabble;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class EngineTest {

    private static Logger log = Logger.getLogger(EngineTest.class.getName());

    @Test
    public void testUtilityMethodForCreatingAListOfWords() {
        assertEquals(list("a"), list("a"));
    }

    @Test
    public void testWordIsFoundAsAnAnagramOfTwoWords() {
        assertEquals(list("hello", "there"), Engine.createWordFrom(
                list("hello", "there"), "therheello").getWords());
        assertEquals(list("a", "b"), Engine
                .createWordFrom(list("a", "b"), "ab").getWords());
        assertEquals(list("a", "b"), Engine
                .createWordFrom(list("a", "b"), "ba").getWords());
    }

    @Test
    public void testWordNotFoundIfInsufficientLetters() {
        assertEquals(null, Engine.createWordFrom(list("a", "b"), "abc")
                .getWords());
    }

    @Test
    public void testWordRejectedIfDoesntRearrangeLettersOfComponentWord() {
        assertNull(Engine.createWordFrom(list("a", "b"), "a").getWords());
        assertEquals(null, Engine.createWordFrom(list("a", "b"), "b")
                .getWords());
    }

    public void testThatAlgorithmDoesntHangOnFindingAnagramsInLotsOfLettersThatAreCommonToWord() {
        long t = System.currentTimeMillis();
        Engine.createWordFrom(listCharacters("ianeri"), "rained");
        log.info("completed first long check");
        Engine.createWordFrom(listCharacters("ianerii"), "rained");
        log.info("completed second long check");
        Engine.createWordFrom(listCharacters("ianeriii"), "rained");
        log.info("completed third long check");
        Engine.createWordFrom(listCharacters("ianeriiiii"), "rained");
        log.info("completed fourth long check");
        Engine.createWordFrom(listCharacters("eaeiaainuri"), "rained");

        assertEquals(Sets.newHashSet(list("l", "o", "r", "e")), Sets
                .newHashSet(Engine.createWordFrom(
                        list("a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                                "k", "l", "m", "n", "o", "r"), "lore")
                        .getWords()));
        // test happened in less than one minute
        Assert.assertTrue((System.currentTimeMillis() - t) / 1000 < 60);
    }

    @Test
    public void testWordNotFoundWhenIsSubstringOfAvailableWords() {
        assertEquals(null, Engine.createWordFrom(list("a", "bc"), "b")
                .getWords());
        assertEquals(null, Engine.createWordFrom(list("a", "bb"), "b")
                .getWords());
    }

    @Test
    public void testWordCannotBeCreatedFromSameWord() {
        Word word = new Word(null, "par", null);
        Iterable<Word> result = Engine.createWordFrom(ImmutableList.of(word),
                "par").getWords();
        assertNull(result);
    }

    @Test
    public void testWordThatIsOneCharacterLongerThanAvailableWordsIsRejected() {
        assertNull(Engine.createWordFrom(list("ab", "ra", "ca", "da", "bra"),
                "abracadabraz").getWords());
    }

    @Test
    public void testWordFoundFromIntersectingParts() {
        Iterable<Word> result = Engine.createWordFrom(
                list("ab", "ra", "ca", "da", "bra"), "abracadabr").getWords();
        assertNull(result);
    }

    @Test
    public void testThatWordSubmittedIsRejectedIfRootIsInHistory() {
        {
            User user = new User("someone", 3);
            Word vote = new Word(user, "vote");
            Word veto = new Word(user, "veto", Lists.newArrayList(vote));
            Word s = new Word(user, "s");
            assertNull(Engine.createWordFrom(Lists.newArrayList(veto, s),
                    "votes").getWords());
            assertNull(Engine.createWordFrom(Lists.newArrayList(vote, s),
                    "votes").getWords());
        }
    }

    @Test
    public void testWordIsNotRejectedIfWordsHaveHistoryAndRootIsNotInHistory() {
        {
            User user = new User("someone", 3);
            Word veto = new Word(user, "veto");
            Word s = new Word(user, "s");
            Assert.assertEquals(Sets.newHashSet(veto, s), Sets
                    .newHashSet(Engine.createWordFrom(
                            Lists.newArrayList(veto, s), "votes").getWords()));
        }
    }

    private Iterable<Word> listCharacters(String s) {
        List<Word> list = Lists.newArrayList();
        for (Character ch : s.toCharArray()) {
            list.add(new Word(new User("anyone", 3), ch + "", null));
        }
        return list;
    }

    private Iterable<Word> list(String... values) {
        List<Word> list = new ArrayList<Word>();
        for (String value : values)
            list.add(new Word(new User("anyone", 3), value, null));
        return list;
    }
}
