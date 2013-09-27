package moten.david.squabble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class Letters {

    public static final String ENGLISH = "eng";
    public static final String SPANISH = "spa";
    private final List<String> list;

    public Letters(String languageCode) {
        list = loadList(languageCode);
    }

    private List<String> loadList(String languageCode) {
        Builder<String> builder = ImmutableList.builder();
        InputStream is = getClass().getResourceAsStream(
                "/letters-" + languageCode + ".txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                if (line.trim().length() > 0) {
                    String[] items = line.split(" ");
                    int frequency = Integer.parseInt(items[1]);
                    String letter = items[0];
                    for (int i = 0; i < frequency; i++)
                        builder.add(letter);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return builder.build();
    }

    /**
     * Returns the letters for an ISO 639-3 language code. The returned list is
     * immutable.
     * 
     * @param language
     * @return
     */
    public List<String> getLetters() {
        return list;
    }

    private void add(List<String> list, String s, int frequency) {
        for (int i = 0; i < frequency; i++)
            list.add(s);
    }

    public static void main(String[] args) {
        System.out.println(new Letters("eng").getLetters());
    }
}
