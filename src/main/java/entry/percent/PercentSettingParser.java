package entry.percent;

import core.field.Field;
import core.field.FieldFactory;
import core.mino.Mino;
import core.mino.MinoFactory;
import core.srs.Rotate;
import entry.CommandLineWrapper;
import entry.NormalCommandLineWrapper;
import entry.PriorityCommandLineWrapper;
import org.apache.commons.cli.*;
import tetfu.Tetfu;
import tetfu.TetfuPage;
import tetfu.common.ColorConverter;
import tetfu.common.ColorType;
import tetfu.field.ColoredField;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PercentSettingParser {
    private static final String CHARSET_NAME = "utf-8";
    private static final String DEFAULT_PATTERNS_TXT = "input/patterns.txt";
    private static final String DEFAULT_FIELD_TXT = "input/field.txt";
    private static final String PATTERN_DELIMITER = ";";
    public static final String SUPPORTED_TETFU_PREFIX = "v115@";

    private final String[] commands;

    public PercentSettingParser(List<String> commands) {
        this.commands = new String[commands.size()];
        commands.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList())
                .toArray(this.commands);
    }

    PercentSettingParser(String commands) {
        this(commands.split(" "));
    }

    private PercentSettingParser(String[] commands) {
        this.commands = commands;
    }

    public Optional<PercentSettings> parse() throws ParseException {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, commands);
        CommandLineWrapper wrapper = new NormalCommandLineWrapper(commandLine);
        PercentSettings settings = new PercentSettings();

        // help
        if (wrapper.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("percent [options]", options);
            return Optional.empty();
        }

        // フィールド・最大削除ラインの設定
        if (wrapper.hasOption("tetfu")) {
            // テト譜から
            Optional<String> tetfuData = wrapper.getStringOption("tetfu");
            assert tetfuData.isPresent();
            String encoded = tetfuData.get();
            String[] commentArgs = loadTetfu(encoded, wrapper, settings);
            CommandLine commandLineTetfu = parser.parse(options, commentArgs);
            wrapper = new PriorityCommandLineWrapper(Arrays.asList(commandLineTetfu, commandLine));
        } else {
            // フィールドファイルから
            Optional<String> fieldPathOption = wrapper.getStringOption("field-path");
            String fieldPath = fieldPathOption.orElse(DEFAULT_FIELD_TXT);
            Path path = Paths.get(fieldPath);
            Charset charset = Charset.forName(CHARSET_NAME);

            try {
                LinkedList<String> fieldLines = Files.lines(path, charset)
                        .map(str -> {
                            if (str.contains("#"))
                                return str.substring(0, str.indexOf('#'));
                            return str;
                        })
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toCollection(LinkedList::new));

                if (fieldLines.isEmpty())
                    throw new IllegalArgumentException("Empty field definition");

                if (fieldLines.get(0).startsWith(SUPPORTED_TETFU_PREFIX)) {
                    // テト譜から
                    String encoded = fieldLines.get(0);
                    String[] commentArgs = loadTetfu(encoded, wrapper, settings);
                    CommandLine commandLineTetfu = parser.parse(options, commentArgs);
                    wrapper = new PriorityCommandLineWrapper(Arrays.asList(commandLineTetfu, commandLine));
                } else {
                    // 最大削除ラインの設定
                    int maxClearLine = Integer.valueOf(fieldLines.pollFirst());
                    settings.setMaxClearLine(maxClearLine);

                    // フィールドの設定
                    String fieldMarks = String.join("", fieldLines);
                    Field field = FieldFactory.createField(fieldMarks);
                    settings.setFieldFilePath(field);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot read Field Height from " + fieldPath);
            } catch (IOException e) {
                throw new IllegalArgumentException("Field file error", e);
            }
        }

        // ホールドの設定
        Optional<Boolean> isUsingHold = wrapper.getBoolOption("hold");
        isUsingHold.ifPresent(settings::setUsingHold);

        // ログファイルの設定
        Optional<String> logFilePath = wrapper.getStringOption("log-path");
        logFilePath.ifPresent(settings::setLogFilePath);

        // 探索パターンの設定
        if (wrapper.hasOption("patterns")) {
            // パターン定義から
            Optional<String> patternOption = wrapper.getStringOption("patterns");
            assert patternOption.isPresent();
            String patternValue = patternOption.get();
            List<String> patterns = Arrays.stream(patternValue.split(PATTERN_DELIMITER)).collect(Collectors.toList());
            settings.setPatterns(patterns);
        } else {
            // パターンファイルから
            Optional<String> patternPathOption = wrapper.getStringOption("patterns-path");
            String patternPath = patternPathOption.orElse(DEFAULT_PATTERNS_TXT);
            Path path = Paths.get(patternPath);
            Charset charset = Charset.forName(CHARSET_NAME);

            try {
                List<String> patterns = Files.lines(path, charset).collect(Collectors.toList());
                settings.setPatterns(patterns);
            } catch (IOException e) {
                throw new IllegalArgumentException("Patterns file error", e);
            }
        }
        return Optional.of(settings);
    }

    private Options createOptions() {
        Options options = new Options();

        Option helpOption = Option.builder("h")
                .optionalArg(true)
                .longOpt("help")
                .desc("Usage")
                .build();
        options.addOption(helpOption);

        Option holdOption = Option.builder("H")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("use or avoid")
                .longOpt("hold")
                .desc("If use hold, set 'use'. If not use hold, set 'avoid'")
                .build();
        options.addOption(holdOption);

        Option tetfuOption = Option.builder("t")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("data-of-tetfu")
                .longOpt("tetfu")
                .desc("Specify tetfu data for s-finder settings")
                .build();
        options.addOption(tetfuOption);

        Option tetfuPageOption = Option.builder("P")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("page-of-tetfu")
                .longOpt("page")
                .desc("Specify pages of tetfu data for s-finder settings")
                .build();
        options.addOption(tetfuPageOption);

        Option fieldFileOption = Option.builder("fp")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("file-path")
                .longOpt("field-path")
                .desc("File path of field definition")
                .build();
        options.addOption(fieldFileOption);

        Option patternOption = Option.builder("p")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("definition")
                .longOpt("patterns")
                .desc("Specify pattern definition, directly")
                .build();
        options.addOption(patternOption);

        Option patternFileOption = Option.builder("pp")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("file-path")
                .longOpt("patterns-path")
                .desc("File path of pattern definition")
                .build();
        options.addOption(patternFileOption);

        Option logFileOption = Option.builder("l")
                .optionalArg(true)
                .hasArg()
                .numberOfArgs(1)
                .argName("file-path")
                .longOpt("log-path")
                .desc("File path of output log")
                .build();
        options.addOption(logFileOption);

        return options;
    }

    private String[] loadTetfu(String encoded, CommandLineWrapper wrapper, PercentSettings settings) {
        // テト譜面のエンコード
        List<TetfuPage> decoded = encodeTetfu(encoded);

        // 指定されたページを抽出
        int page = wrapper.getIntegerOption("page").orElse(1);
        TetfuPage tetfuPage = extractTetfuPage(decoded, page);

        // コメントの抽出
        List<String> splitComment = Arrays.stream(tetfuPage.getComment().split(" "))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (splitComment.size() < 1)
            throw new IllegalArgumentException(String.format("Cannot max-clear-line in comment of tetfu in %d pages", page));

        String firstComment = splitComment.remove(0);
        int maxClearLine;
        try {
            maxClearLine = Integer.valueOf(firstComment);  // 最大削除ラインの取得
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid number for max-clear-line in comment of tetfu");
        }

        // 最大削除ラインの設定
        if (maxClearLine < 1)
            throw new IllegalArgumentException("Should be 1 <= max-clear-line in comment of tetfu");
        settings.setMaxClearLine(maxClearLine);

        // フィールドを設定
        ColoredField coloredField = tetfuPage.getField();
        if (tetfuPage.isPutMino()) {
            ColorType colorType = tetfuPage.getColorType();
            Rotate rotate = tetfuPage.getRotate();
            int x = tetfuPage.getX();
            int y = tetfuPage.getY();

            ColorConverter colorConverter = new ColorConverter();
            Mino mino = new Mino(colorConverter.parseToBlock(colorType), rotate);
            coloredField.putMino(mino, x, y);
            coloredField.clearLine();
        }
        settings.setField(coloredField, maxClearLine);

        // コマンド引数を配列に変換
        String[] newArgs = new String[splitComment.size()];
        splitComment.toArray(newArgs);

        return newArgs;
    }

    private List<TetfuPage> encodeTetfu(String encoded) {
        MinoFactory minoFactory = new MinoFactory();
        ColorConverter colorConverter = new ColorConverter();
        Tetfu tetfu = new Tetfu(minoFactory, colorConverter);
        if (encoded.startsWith(SUPPORTED_TETFU_PREFIX)) {
            return tetfu.decode(encoded.substring(5));
        } else {
            throw new UnsupportedOperationException("Unsupported tetfu older than v115");
        }
    }

    private TetfuPage extractTetfuPage(List<TetfuPage> tetfuPages, int page) {
        if (page < 1) {
            throw new IllegalArgumentException(String.format("Option[page=%d]: Should 1 <= page of tetfu", page));
        } else if (page <= tetfuPages.size()) {
            return tetfuPages.get(page - 1);
        } else {
            throw new IllegalArgumentException(String.format("Option[page=%d]: Over page", page));
        }
    }
}