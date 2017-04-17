package entry;

import core.field.Field;
import core.field.FieldFactory;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class CheckerEntryTest {
    @Test
    public void invokeUsingHoldJust() throws Exception {
        // Field
        String marks = "" +
                "XX________" +
                "XX________" +
                "XXX______X" +
                "XXXXXXX__X" +
                "XXXXXX___X" +
                "XXXXXXX_XX" +
                "";
        int maxClearLine = 6;

        List<String> patterns = Collections.singletonList("*p7");

        Field field = FieldFactory.createField(marks);
        StringWriter writer = new StringWriter();
        Settings settings = new Settings();
        CheckerEntry entry = new CheckerEntry(writer, settings, false);

        entry.invoke(field, patterns, maxClearLine);

        String output = writer.toString();

        String expected = "success = 99.96% (5038/5040)";
        assertThat(output, containsString(expected));
    }

    @Test
    public void invokeUsingHoldOver() throws Exception {
        // Field
        String marks = "" +
                "XX_____XXX" +
                "XXX____XXX" +
                "XXXX___XXX" +
                "XXX____XXX" +
                "";
        int maxClearLine = 4;

        List<String> patterns = Arrays.asList(
                "I, T, [IOJLSZ]p3",
                "I, I, [TOJLSZ]p3",
                "I, O, [TIJLSZ]p3",
                "I, J, [TIOLSZ]p3",
                "I, L, [TIOJSZ]p3",
                "I, S, [TIOJLZ]p3",
                "I, Z, [TIOJLS]p3"
        );

        Field field = FieldFactory.createField(marks);
        StringWriter writer = new StringWriter();
        Settings settings = new Settings();
        CheckerEntry entry = new CheckerEntry(writer, settings, false);

        entry.invoke(field, patterns, maxClearLine);

        String output = writer.toString();

        String expected = "success = 84.64% (711/840)";
        assertThat(output, containsString(expected));
    }

    @Test
    public void invokeNoHoldJust() throws Exception {
        // Field
        String marks = "" +
                "X________X" +
                "X________X" +
                "XX______XX" +
                "XXXXXX__XX" +
                "";
        int maxClearLine = 4;

        List<String> patterns = Collections.singletonList("*p6");

        Field field = FieldFactory.createField(marks);
        StringWriter writer = new StringWriter();
        Settings settings = new Settings();
        settings.setUsingHold(false);
        CheckerEntry entry = new CheckerEntry(writer, settings, false);

        entry.invoke(field, patterns, maxClearLine);

        String output = writer.toString();

        String expected = "success = 28.55% (1439/5040)";
        assertThat(output, containsString(expected));
    }

    @Test
    public void invokeNoHoldOver() throws Exception {
        // Field
        String marks = "" +
                "X_________" +
                "X___X_____" +
                "XXXXXXX___" +
                "XXXXXX____" +
                "";
        int maxClearLine = 4;

        List<String> patterns = Collections.singletonList("*p7");

        Field field = FieldFactory.createField(marks);
        StringWriter writer = new StringWriter();
        Settings settings = new Settings();
        settings.setUsingHold(false);
        CheckerEntry entry = new CheckerEntry(writer, settings, false);

        entry.invoke(field, patterns, maxClearLine);

        String output = writer.toString();

        String expected = "success = 14.42% (727/5040)";
        assertThat(output, containsString(expected));
    }
}