package com.paic.sync.feature.bert;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.io.Resources;

/**
 * description: FullTokenizer
 * date: 2020/11/12 8:47 下午
 * author: gallup
 * version: 1.0
 */
public class FullTokenizer extends Tokenizer{
    private static final boolean DEFAULT_DO_LOWER_CASE = false;

    private static Map<String, Integer> loadVocabulary(final Path file) {
        final Map<String, Integer> vocabulary = new HashMap<>();
        try(BufferedReader reader = Files.newBufferedReader(file, Charset.forName("UTF-8"))) {
            int index = 0;
            String line;
            while((line = reader.readLine()) != null) {
                vocabulary.put(line.trim(), index++);
            }
        } catch(final IOException e) {
            throw new RuntimeException(e);
        }
        return vocabulary;
    }

    private static Path toPath(final String resource) {
        try {
            return Paths.get(Resources.getResource(resource).toURI());
        } catch(final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private final BasicTokenizer basic;
    private final Map<String, Integer> vocabulary;
    private final WordpieceTokenizer wordpiece;

    /**
     * Creates a BERT
     *
     * @param vocabulary
     *        the BERT vocabulary file to use for tokenization
     * @since 1.0.3
     */
    public FullTokenizer(final File vocabulary) {
        this(Paths.get(vocabulary.toURI()), DEFAULT_DO_LOWER_CASE);
    }

    /**
     * Creates a BERT
     *
     * @param vocabulary
     *        the BERT vocabulary file to use for tokenization
     * @param doLowerCase
     *        whether to convert sequences to lower case during tokenization
     * @since 1.0.3
     */
    public FullTokenizer(final File vocabulary, final boolean doLowerCase) {
        this(Paths.get(vocabulary.toURI()), doLowerCase);
    }

    /**
     * Creates a BERT
     *
     * @param vocabularyPath
     *        the path to the BERT vocabulary file to use for tokenization
     * @since 1.0.3
     */
    public FullTokenizer(final Path vocabularyPath) {
        this(vocabularyPath, DEFAULT_DO_LOWER_CASE);
    }

    /**
     * Creates a BERT
     *
     * @param vocabularyPath
     *        the path to the BERT vocabulary file to use for tokenization
     * @param doLowerCase
     *        whether to convert sequences to lower case during tokenization
     * @since 1.0.3
     */
    public FullTokenizer(final Path vocabularyPath, final boolean doLowerCase) {
        vocabulary = loadVocabulary(vocabularyPath);
        basic = new BasicTokenizer(doLowerCase);
        wordpiece = new WordpieceTokenizer(vocabulary);
    }

    /**
     * Creates a BERT
     *
     * @param vocabularyResource
     *        the resource path to the BERT vocabulary file to use for tokenization
     * @since 1.0.3
     */
    public FullTokenizer(final String vocabularyResource) {
        this(toPath(vocabularyResource), DEFAULT_DO_LOWER_CASE);
    }

    /**
     * Creates a BERT
     *
     * @param vocabularyResource
     *        the resource path to the BERT vocabulary file to use for tokenization
     * @param doLowerCase
     *        whether to convert sequences to lower case during tokenization
     * @since 1.0.3
     */
    public FullTokenizer(final String vocabularyResource, final boolean doLowerCase) {
        this(toPath(vocabularyResource), doLowerCase);
    }

    /**
     * Converts BERT sub-tokens into their inputIds
     *
     * @param tokens
     *        the tokens to convert
     * @return the inputIds for the tokens
     * @since 1.0.3
     */
    public int[] convert(final String[] tokens) {
        return Arrays.stream(tokens).mapToInt(vocabulary::get).toArray();
    }

    @Override
    public String[] tokenize(final String sequence) {
        return Arrays.stream(wordpiece.tokenize(basic.tokenize(sequence)))
                .flatMap(Stream::of)
                .toArray(String[]::new);
    }

    @Override
    public String[][] tokenize(final String... sequences) {
        return Arrays.stream(basic.tokenize(sequences))
                .map((final String[] tokens) -> Arrays.stream(wordpiece.tokenize(tokens))
                        .flatMap(Stream::of)
                        .toArray(String[]::new))
                .toArray(String[][]::new);
    }
}
