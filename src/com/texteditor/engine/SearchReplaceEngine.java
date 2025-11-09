package com.texteditor.engine;

import java.util.*;
import java.util.regex.*;

/**
 * Handles find and replace operations with regex support.
 */
public class SearchReplaceEngine {

    public enum SearchOption {
        CASE_SENSITIVE, WHOLE_WORDS_ONLY, USE_REGEX
    }

    private Set<SearchOption> options;

    public SearchReplaceEngine() {
        this.options = new HashSet<>();
    }

    public void setOption(SearchOption option, boolean enabled) {
        if (enabled) {
            options.add(option);
        } else {
            options.remove(option);
        }
    }

    public List<Integer> findAll(String text, String searchTerm) {
        List<Integer> results = new ArrayList<>();

        try {
            String pattern = buildPattern(searchTerm);
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(text);

            while (m.find()) {
                results.add(m.start());
            }
        } catch (PatternSyntaxException e) {
            // Invalid regex pattern
            if (!options.contains(SearchOption.USE_REGEX)) {
                results = findAllLiteral(text, searchTerm);
            }
        }

        return results;
    }

    private List<Integer> findAllLiteral(String text, String searchTerm) {
        List<Integer> results = new ArrayList<>();
        String searchText = options.contains(SearchOption.CASE_SENSITIVE) ? text : text.toLowerCase();
        String term = options.contains(SearchOption.CASE_SENSITIVE) ? searchTerm : searchTerm.toLowerCase();

        int index = 0;
        while ((index = searchText.indexOf(term, index)) != -1) {
            results.add(index);
            index += term.length();
        }

        return results;
    }

    private String buildPattern(String searchTerm) {
        StringBuilder pattern = new StringBuilder();

        if (options.contains(SearchOption.WHOLE_WORDS_ONLY)) {
            pattern.append("\\b");
        }

        pattern.append(options.contains(SearchOption.USE_REGEX) ? searchTerm : Pattern.quote(searchTerm));

        if (options.contains(SearchOption.WHOLE_WORDS_ONLY)) {
            pattern.append("\\b");
        }

        int flags = options.contains(SearchOption.CASE_SENSITIVE) ? 0 : Pattern.CASE_INSENSITIVE;
        Pattern.compile(pattern.toString(), flags);

        return pattern.toString();
    }

    public String replaceFirst(String text, String searchTerm, String replacement) {
        List<Integer> matches = findAll(text, searchTerm);
        if (matches.isEmpty()) return text;

        int matchStart = matches.get(0);
        int matchEnd = matchStart + getMatchLength(text, searchTerm, matchStart);

        return text.substring(0, matchStart) + replacement + text.substring(matchEnd);
    }

    public String replaceAll(String text, String searchTerm, String replacement) {
        String result = text;
        List<Integer> matches = findAll(text, searchTerm);

        // Replace from end to start to maintain indices
        for (int i = matches.size() - 1; i >= 0; i--) {
            int matchStart = matches.get(i);
            int matchEnd = matchStart + getMatchLength(text, searchTerm, matchStart);
            result = result.substring(0, matchStart) + replacement + result.substring(matchEnd);
        }

        return result;
    }

    private int getMatchLength(String text, String searchTerm, int position) {
        return text.substring(position).indexOf(searchTerm) + searchTerm.length();
    }
}
