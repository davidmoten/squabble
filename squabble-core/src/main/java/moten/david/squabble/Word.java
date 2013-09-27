package moten.david.squabble;

import java.util.ArrayList;
import java.util.List;

public class Word {

    @Override
    public String toString() {
        return word;
    }

    private final String word;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((madeFrom == null) ? 0 : madeFrom.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        result = prime * result + ((word == null) ? 0 : word.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Word other = (Word) obj;
        if (madeFrom == null) {
            if (other.madeFrom != null)
                return false;
        } else if (!madeFrom.equals(other.madeFrom))
            return false;
        if (owner == null) {
            if (other.owner != null)
                return false;
        } else if (!owner.equals(other.owner))
            return false;
        if (word == null) {
            if (other.word != null)
                return false;
        } else if (!word.equals(other.word))
            return false;
        return true;
    }

    private final User owner;
    private final List<Word> madeFrom;

    public String getWord() {
        return word;
    }

    public User getOwner() {
        return owner;
    }

    public List<Word> getMadeFrom() {
        return madeFrom;
    }

    public Word(User user, String word) {
        this(user, word, new ArrayList<Word>());
    }

    public Word(User owner, String word, List<Word> madeFrom) {
        super();
        this.owner = owner;
        this.word = word;
        this.madeFrom = madeFrom;
    }
}
