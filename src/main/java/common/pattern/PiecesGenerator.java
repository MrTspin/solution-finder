package common.pattern;


import common.SyntaxException;
import common.datastore.pieces.Pieces;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PiecesGenerator implements Iterable<Pieces> {
    public static void verify(String pattern) throws SyntaxException {
        verify(Collections.singletonList(pattern));
    }

    public static void verify(List<String> patterns) throws SyntaxException {
        int depth = -1;
        for (int index = 0; index < patterns.size(); index++) {
            String pattern = patterns.get(index);
            if (pattern.contains("#"))
                pattern = pattern.substring(0, pattern.indexOf('#'));

            pattern = pattern.trim();

            if (pattern.equals(""))
                continue;

            int currentDepth = 0;
            String[] splits = pattern.split(",");
            for (String split : splits) {
                try {
                    currentDepth += PatternElement.verify(split);
                } catch (SyntaxException e) {
                    String message = String.format("'%s' # '%s' in %d line : cause = %s", split.trim(), pattern.trim(), index + 1, e.getMessage());
                    throw new SyntaxException(message);
                }
            }

            if (depth == -1) {
                depth = currentDepth;  // First depth
            } else if (depth != currentDepth) {
                String message = String.format("'%s' in %d line : cause = %s", pattern.trim(), index + 1, "Num of blocks is not equal to others");
                throw new SyntaxException(message);
            }
        }
    }

    private final List<String> patterns;

    public PiecesGenerator(String pattern) {
        this(Collections.singletonList(pattern));
    }

    public PiecesGenerator(List<String> patterns) {
        this.patterns = patterns.stream()
                .map(str -> {
                    if (str.contains("#"))
                        return str.substring(0, str.indexOf('#'));
                    return str;
                })
                .map(String::trim)
                .filter(str -> !str.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public Iterator<Pieces> iterator() {
        return new PiecesIterator(patterns);
    }

    public int getDepth() {
        return new PiecesIterator(patterns).getDepths();
    }

    public Stream<Pieces> stream() {
        Stream<Pieces> stream = Stream.empty();
        for (String pattern : patterns)
            stream = Stream.concat(stream, new PiecesStreamBuilder(pattern).stream());
        return stream;
    }
}


