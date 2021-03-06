package entry;

import entry.path.PathEntryPoint;
import entry.path.PathSettingParser;
import entry.path.PathSettings;
import entry.percent.PercentEntryPoint;
import entry.percent.PercentSettingParser;
import entry.percent.PercentSettings;
import entry.util.fig.FigUtilEntryPoint;
import entry.util.fig.FigUtilSettingParser;
import entry.util.fig.FigUtilSettings;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// TODO: all below
public class EntryPointMain {
    public static void main(String[] args) throws Exception {
        if (args.length < 1)
            throw new IllegalArgumentException("No command: Use percent, path");

        if (args[0].equals("-h")) {
            System.out.println("Usage: <command> [options]");
            System.out.println("  command: percent, path, util fig");
            System.exit(0);
        }

        if (args[0].equals("-v")) {
            System.out.println("Version: 0.43");
            System.exit(0);
        }

        // 引数リストの作成
        List<String> argsList = new ArrayList<>(Arrays.asList(args).subList(1, args.length));

        // 実行を振り分け
        EntryPoint entryPoint = createEntryPoint(args[0], argsList);
        entryPoint.run();
        entryPoint.close();
    }

    private static EntryPoint createEntryPoint(String type, List<String> commands) throws ParseException, IOException {
        // 実行を振り分け
        switch (type) {
            case "percent":
                return getPercentEntryPoint(commands);
            case "path":
                return getPathEntryPoint(commands);
            case "util":
                return getUtilEntryPoint(commands);
            default:
                throw new IllegalArgumentException("Invalid type: Use percent, path, util");
        }
    }

    private static EntryPoint getPercentEntryPoint(List<String> commands) throws ParseException, IOException {
        PercentSettingParser settingParser = new PercentSettingParser(commands);
        Optional<PercentSettings> settings = settingParser.parse();

        if (!settings.isPresent())
            System.exit(0);

        return new PercentEntryPoint(settings.get());
    }

    private static EntryPoint getPathEntryPoint(List<String> commands) throws ParseException, IOException {
        PathSettingParser settingParser = new PathSettingParser(commands);
        Optional<PathSettings> settings = settingParser.parse();

        if (!settings.isPresent())
            System.exit(0);

        return new PathEntryPoint(settings.get());
    }

    private static EntryPoint getUtilEntryPoint(List<String> commands) throws ParseException {
        if (!commands.get(0).equals("fig"))
            throw new IllegalArgumentException("util: Invalid type: Use fig");

        List<String> figCommands = commands.subList(1, commands.size());
        FigUtilSettingParser settingParser = new FigUtilSettingParser(figCommands);
        Optional<FigUtilSettings> settings = settingParser.parse();

        if (!settings.isPresent())
            System.exit(0);

        return new FigUtilEntryPoint(settings.get());
    }
}
