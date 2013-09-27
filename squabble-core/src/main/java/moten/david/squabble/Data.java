package moten.david.squabble;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Wraps the list of words per user.
 * 
 * @author dave
 * 
 */
public class Data {
    private final ImmutableListMultimap<User, Word> map;

    /**
     * Constructor.
     * 
     * @param map
     */
    public Data(ListMultimap<User, Word> map) {
        this.map = ImmutableListMultimap.copyOf(map);
    }

    /**
     * Returns the map of users to word lists.
     * 
     * @return
     */
    public ImmutableListMultimap<User, Word> getMap() {
        return map;
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        for (User user : map.keySet()) {
            if (s.length() > 0)
                s.append('\n');
            s.append(user.getName() + ":");
            for (Word word : map.get(user))
                s.append(" " + word.getWord());
        }
        return s.toString();
    }
}
