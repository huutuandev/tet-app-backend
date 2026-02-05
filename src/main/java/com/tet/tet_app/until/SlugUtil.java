package com.tet.tet_app.until;

import java.text.Normalizer;
import java.util.Locale;

public class SlugUtil {

    public static String toSlug(String input) {
        String slug = Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");

        return slug;
    }
}

