package core.field;

public class ColumnOperators {
    // 列を表すビット列をFieldのビット列に変換 (width=1,height=6 の 6bitまで)
    @SuppressWarnings("ConstantConditions")
    public static long parseToBoardWidth6(int column) {
        switch (column) {
            case 63:
                return 1127000493261825L;
            case 31:
                return 1100586419201L;
            case 47:
                return 1125900981634049L;
            case 55:
                return 1126999419520001L;
            case 59:
                return 1127000492213249L;
            case 61:
                return 1127000493260801L;
            case 62:
                return 1127000493261824L;
            case 15:
                return 1074791425L;
            case 23:
                return 1099512677377L;
            case 27:
                return 1100585370625L;
            case 29:
                return 1100586418177L;
            case 30:
                return 1100586419200L;
            case 39:
                return 1125899907892225L;
            case 43:
                return 1125900980585473L;
            case 45:
                return 1125900981633025L;
            case 46:
                return 1125900981634048L;
            case 51:
                return 1126999418471425L;
            case 53:
                return 1126999419518977L;
            case 54:
                return 1126999419520000L;
            case 57:
                return 1127000492212225L;
            case 58:
                return 1127000492213248L;
            case 60:
                return 1127000493260800L;
            case 7:
                return 1049601L;
            case 11:
                return 1073742849L;
            case 13:
                return 1074790401L;
            case 14:
                return 1074791424L;
            case 19:
                return 1099511628801L;
            case 21:
                return 1099512676353L;
            case 22:
                return 1099512677376L;
            case 25:
                return 1100585369601L;
            case 26:
                return 1100585370624L;
            case 28:
                return 1100586418176L;
            case 35:
                return 1125899906843649L;
            case 37:
                return 1125899907891201L;
            case 38:
                return 1125899907892224L;
            case 41:
                return 1125900980584449L;
            case 42:
                return 1125900980585472L;
            case 44:
                return 1125900981633024L;
            case 49:
                return 1126999418470401L;
            case 50:
                return 1126999418471424L;
            case 52:
                return 1126999419518976L;
            case 56:
                return 1127000492212224L;
            case 3:
                return 1025L;
            case 5:
                return 1048577L;
            case 6:
                return 1049600L;
            case 9:
                return 1073741825L;
            case 10:
                return 1073742848L;
            case 12:
                return 1074790400L;
            case 17:
                return 1099511627777L;
            case 18:
                return 1099511628800L;
            case 20:
                return 1099512676352L;
            case 24:
                return 1100585369600L;
            case 33:
                return 1125899906842625L;
            case 34:
                return 1125899906843648L;
            case 36:
                return 1125899907891200L;
            case 40:
                return 1125900980584448L;
            case 48:
                return 1126999418470400L;
            case 1:
                return 1L;
            case 2:
                return 1024L;
            case 4:
                return 1048576L;
            case 8:
                return 1073741824L;
            case 16:
                return 1099511627776L;
            case 32:
                return 1125899906842624L;
            case 0:
                return 0L;
        }
        throw new IllegalStateException("Board over 6bit");
    }

    // 列を表すビット列を反転したFieldのビット列に変換 (width=1,height=6 の 6bitまで)
    @SuppressWarnings("ConstantConditions")
    public static long parseToInvertedBoardWidth6(int column) {
        switch (column) {
            case 63:
                return 0L;
            case 31:
                return 1125899906842624L;
            case 47:
                return 1099511627776L;
            case 55:
                return 1073741824L;
            case 59:
                return 1048576L;
            case 61:
                return 1024L;
            case 62:
                return 1L;
            case 15:
                return 1126999418470400L;
            case 23:
                return 1125900980584448L;
            case 27:
                return 1125899907891200L;
            case 29:
                return 1125899906843648L;
            case 30:
                return 1125899906842625L;
            case 39:
                return 1100585369600L;
            case 43:
                return 1099512676352L;
            case 45:
                return 1099511628800L;
            case 46:
                return 1099511627777L;
            case 51:
                return 1074790400L;
            case 53:
                return 1073742848L;
            case 54:
                return 1073741825L;
            case 57:
                return 1049600L;
            case 58:
                return 1048577L;
            case 60:
                return 1025L;
            case 7:
                return 1127000492212224L;
            case 11:
                return 1126999419518976L;
            case 13:
                return 1126999418471424L;
            case 14:
                return 1126999418470401L;
            case 19:
                return 1125900981633024L;
            case 21:
                return 1125900980585472L;
            case 22:
                return 1125900980584449L;
            case 25:
                return 1125899907892224L;
            case 26:
                return 1125899907891201L;
            case 28:
                return 1125899906843649L;
            case 35:
                return 1100586418176L;
            case 37:
                return 1100585370624L;
            case 38:
                return 1100585369601L;
            case 41:
                return 1099512677376L;
            case 42:
                return 1099512676353L;
            case 44:
                return 1099511628801L;
            case 49:
                return 1074791424L;
            case 50:
                return 1074790401L;
            case 52:
                return 1073742849L;
            case 56:
                return 1049601L;
            case 3:
                return 1127000493260800L;
            case 5:
                return 1127000492213248L;
            case 6:
                return 1127000492212225L;
            case 9:
                return 1126999419520000L;
            case 10:
                return 1126999419518977L;
            case 12:
                return 1126999418471425L;
            case 17:
                return 1125900981634048L;
            case 18:
                return 1125900981633025L;
            case 20:
                return 1125900980585473L;
            case 24:
                return 1125899907892225L;
            case 33:
                return 1100586419200L;
            case 34:
                return 1100586418177L;
            case 36:
                return 1100585370625L;
            case 40:
                return 1099512677377L;
            case 48:
                return 1074791425L;
            case 1:
                return 1127000493261824L;
            case 2:
                return 1127000493260801L;
            case 4:
                return 1127000492213249L;
            case 8:
                return 1126999419520001L;
            case 16:
                return 1125900981634049L;
            case 32:
                return 1100586419201L;
            case 0:
                return 1127000493261825L;
        }
        throw new IllegalStateException("Board over 6bit");
    }
}
