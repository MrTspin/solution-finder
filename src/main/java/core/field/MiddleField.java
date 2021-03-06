package core.field;

import common.comparator.FieldComparator;
import core.mino.Mino;

/**
 * フィールドの高さ height <= 12 であること
 * マルチスレッド非対応
 */
public class MiddleField implements Field {
    private static final int FIELD_WIDTH = 10;
    private static final int MAX_FIELD_HEIGHT = 12;
    private static final int FIELD_ROW_BOARDER_Y = 6;

    private long xBoardLow = 0; // x,y: 最下位 (0,0), (1,0),  ... , (9,0), (0,1), ... 最上位 // フィールド範囲外は必ず0であること
    private long xBoardHigh = 0; // x,y: 最下位 (0,0), (1,0),  ... , (9,0), (0,1), ... 最上位 // フィールド範囲外は必ず0であること

    public MiddleField() {
    }

    private MiddleField(MiddleField src) {
        this.xBoardLow = src.xBoardLow;
        this.xBoardHigh = src.xBoardHigh;
    }

    public MiddleField(long xBoardLow, long xBoardHigh) {
        this.xBoardLow = xBoardLow;
        this.xBoardHigh = xBoardHigh;
    }

    long getXBoardLow() {
        return xBoardLow;
    }

    long getXBoardHigh() {
        return xBoardHigh;
    }

    @Override
    public int getMaxFieldHeight() {
        return MAX_FIELD_HEIGHT;
    }

    @Override
    public void setBlock(int x, int y) {
        if (y < FIELD_ROW_BOARDER_Y)
            xBoardLow |= getXMask(x, y);
        else
            xBoardHigh |= getXMask(x, y - FIELD_ROW_BOARDER_Y);
    }

    @Override
    public void removeBlock(int x, int y) {
        if (y < FIELD_ROW_BOARDER_Y)
            xBoardLow &= ~getXMask(x, y);
        else
            xBoardHigh &= ~getXMask(x, y - FIELD_ROW_BOARDER_Y);
    }

    private long getXMask(int x, int y) {
        return 1L << x + y * FIELD_WIDTH;
    }

    @Override
    public void putMino(Mino mino, int x, int y) {
        // Lowの更新が必要
        if (y + mino.getMinY() < FIELD_ROW_BOARDER_Y)
            xBoardLow |= mino.getMask(x, y);

        // Highの更新が必要
        if (FIELD_ROW_BOARDER_Y <= y + mino.getMaxY())
            xBoardHigh |= mino.getMask(x, y - FIELD_ROW_BOARDER_Y);
    }

    @Override
    public void removeMino(Mino mino, int x, int y) {
        // Lowの更新が必要
        if (y + mino.getMinY() < FIELD_ROW_BOARDER_Y)
            xBoardLow &= ~mino.getMask(x, y);

        // Highの更新が必要
        if (FIELD_ROW_BOARDER_Y <= y + mino.getMaxY())
            xBoardHigh &= ~mino.getMask(x, y - FIELD_ROW_BOARDER_Y);
    }

    @Override
    public int getYOnHarddrop(Mino mino, int x, int startY) {
        int min = -mino.getMinY();
        for (int y = startY - 1; min <= y; y--)
            if (!canPutMino(mino, x, y))
                return y + 1;
        return min;
    }

    @Override
    public boolean canReachOnHarddrop(Mino mino, int x, int startY) {
        int max = MAX_FIELD_HEIGHT - mino.getMinY();
        for (int y = startY + 1; y < max; y++)
            if (!canPutMino(mino, x, y))
                return false;
        return true;
    }

    @Override
    public boolean isEmpty(int x, int y) {
        if (y < FIELD_ROW_BOARDER_Y)
            return (xBoardLow & getXMask(x, y)) == 0L;
        else
            return (xBoardHigh & getXMask(x, y - FIELD_ROW_BOARDER_Y)) == 0L;
    }

    @Override
    public boolean existsAbove(int y) {
        if (MAX_FIELD_HEIGHT <= y) {
            return false;
        } else if (FIELD_ROW_BOARDER_Y <= y) {
            // Highで完結
            long mask = 0xffffffffffL << (y - FIELD_ROW_BOARDER_Y) * FIELD_WIDTH;
            return (xBoardHigh & mask) != 0L;
        } else {
            // すべて必要
            // Highのチェック
            if (xBoardHigh != 0L)
                return true;

            // Lowのチェック
            long mask = 0xffffffffffL << y * FIELD_WIDTH;
            return (xBoardLow & mask) != 0L;
        }
    }

    @Override
    public boolean isPerfect() {
        return xBoardLow == 0L && xBoardHigh == 0L;
    }

    @Override
    public boolean isFilledInColumn(int x, int maxY) {
        if (maxY == 0) {
            return true;
        } else if (maxY <= FIELD_ROW_BOARDER_Y) {
            // Lowで完結
            long mask = BitOperators.getColumnOneLineBelowY(maxY) << x;
            return (~xBoardLow & mask) == 0L;
        } else {
            // すべて必要
            // Lowのチェック
            long maskLow = BitOperators.getColumnOneLineBelowY(FIELD_ROW_BOARDER_Y) << x;
            if ((~xBoardLow & maskLow) != 0L)
                return false;

            // Highのチェック
            long maskHigh = BitOperators.getColumnOneLineBelowY(maxY - FIELD_ROW_BOARDER_Y) << x;
            return (~xBoardHigh & maskHigh) == 0L;
        }
    }

    @Override
    public boolean isWallBetweenLeft(int x, int maxY) {
        if (maxY == 0) {
            return true;
        } else if (maxY <= FIELD_ROW_BOARDER_Y) {
            // Lowで完結
            return isWallBetweenLeft(x, maxY, xBoardLow);
        } else {
            // すべて必要
            // Lowのチェック
            if (!isWallBetweenLeft(x, FIELD_ROW_BOARDER_Y, xBoardLow))
                return false;

            // Highのチェック
            return isWallBetweenLeft(x, maxY - FIELD_ROW_BOARDER_Y, xBoardHigh);
        }
    }

    private boolean isWallBetweenLeft(int x, int maxYHigh, long xBoardHigh) {
        long maskHigh = BitOperators.getColumnOneLineBelowY(maxYHigh);
        long reverseXBoardHigh = ~xBoardHigh;
        long columnHigh = maskHigh << x;
        long rightHigh = reverseXBoardHigh & columnHigh;
        long leftHigh = reverseXBoardHigh & (columnHigh >>> 1);
        return ((leftHigh << 1) & rightHigh) == 0L;
    }

    @Override
    public boolean canPutMino(Mino mino, int x, int y) {
        if (y + mino.getMaxY() < FIELD_ROW_BOARDER_Y) {
            // Lowで完結
            return (xBoardLow & mino.getMask(x, y)) == 0L;
        } else if (FIELD_ROW_BOARDER_Y <= y + mino.getMinY()) {
            // Highで完結
            return (xBoardHigh & mino.getMask(x, y - FIELD_ROW_BOARDER_Y)) == 0L;
        } else {
            // 分割
            return (xBoardLow & mino.getMask(x, y)) == 0L & (xBoardHigh & mino.getMask(x, y - FIELD_ROW_BOARDER_Y)) == 0L;
        }
    }

    @Override
    public boolean isOnGround(Mino mino, int x, int y) {
        return y <= -mino.getMinY() || !canPutMino(mino, x, y - 1);
    }

    @Override
    public int getBlockCountBelowOnX(int x, int maxY) {
        if (maxY <= FIELD_ROW_BOARDER_Y) {
            // Lowで完結
            long mask = BitOperators.getColumnOneLineBelowY(maxY) << x;
            return Long.bitCount(xBoardLow & mask);
        } else {
            // すべて必要
            long maskLow = BitOperators.getColumnOneLineBelowY(FIELD_ROW_BOARDER_Y) << x;
            long maskHigh = BitOperators.getColumnOneLineBelowY(maxY - FIELD_ROW_BOARDER_Y) << x;
            return Long.bitCount(xBoardLow & maskLow) + Long.bitCount(xBoardHigh & maskHigh);
        }
    }

    // TODO: unittest
    @Override
    public int getBlockCountOnY(int y) {
        if (y < 6) {
            long mask = 0x3ff << y * FIELD_WIDTH;
            return Long.bitCount(xBoardLow & mask);
        } else {
            long mask = 0x3ff << (y - 6) * FIELD_WIDTH;
            return Long.bitCount(xBoardHigh & mask);
        }
    }

    @Override
    public int getNumOfAllBlocks() {
        return Long.bitCount(xBoardLow) + Long.bitCount(xBoardHigh);
    }

    @Override
    public int clearLine() {
        long deleteKey = clearLineReturnKey();
        return Long.bitCount(deleteKey);
    }

    @Override
    public long clearLineReturnKey() {
        long deleteKeyLow = KeyOperators.getDeleteKey(xBoardLow);
        long newXBoardLow = LongBoardMap.deleteLine(xBoardLow, deleteKeyLow);

        long deleteKeyHigh = KeyOperators.getDeleteKey(xBoardHigh);
        long newXBoardHigh = LongBoardMap.deleteLine(xBoardHigh, deleteKeyHigh);

        int deleteLineLow = Long.bitCount(deleteKeyLow);

        this.xBoardLow = (newXBoardLow | (newXBoardHigh << (6 - deleteLineLow) * 10)) & 0xfffffffffffffffL;
        this.xBoardHigh = newXBoardHigh >>> deleteLineLow * 10;

        return deleteKeyLow | (deleteKeyHigh << 1);
    }

    @Override
    public void insertBlackLineWithKey(long deleteKey) {
        long deleteKeyLow = deleteKey & 0x4010040100401L;
        int deleteLineLow = Long.bitCount(deleteKeyLow);
        int leftLineLow = 6 - deleteLineLow;
        long newXBoardLow = LongBoardMap.insertBlackLine(xBoardLow & BitOperators.getRowMaskBelowY(leftLineLow), deleteKeyLow);

        long deleteKeyHigh = (deleteKey & 0x8020080200802L) >> 1;
        long newXBoardHigh = LongBoardMap.insertBlackLine((xBoardHigh << 10 * deleteLineLow) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLineLow)) >> 10 * leftLineLow), deleteKeyHigh);

        this.xBoardLow = newXBoardLow;
        this.xBoardHigh = newXBoardHigh & 0xfffffffffffffffL;
    }

    @Override
    public void insertWhiteLineWithKey(long deleteKey) {
        long deleteKeyLow = deleteKey & 0x4010040100401L;
        int deleteLineLow = Long.bitCount(deleteKeyLow);
        int leftLineLow = 6 - deleteLineLow;
        long newXBoardLow = LongBoardMap.insertWhiteLine(xBoardLow & BitOperators.getRowMaskBelowY(leftLineLow), deleteKeyLow);

        long deleteKeyHigh = (deleteKey & 0x8020080200802L) >> 1;
        long newXBoardHigh = LongBoardMap.insertWhiteLine((xBoardHigh << 10 * deleteLineLow) | ((xBoardLow & BitOperators.getRowMaskAboveY(leftLineLow)) >> 10 * leftLineLow), deleteKeyHigh);

        this.xBoardLow = newXBoardLow;
        this.xBoardHigh = newXBoardHigh & 0xfffffffffffffffL;
    }

    @Override
    public int getBoardCount() {
        return 2;
    }

    @Override
    public long getBoard(int index) {
        assert 0 <= index && index < 2 : index;
        if (index == 0)
            return xBoardLow;
        return xBoardHigh;
    }

    @Override
    public Field freeze(int maxHeight) {
        assert 0 < maxHeight && maxHeight <= 12;
        if (maxHeight <= 6)
            return new SmallField(xBoardLow);
        return new MiddleField(this);
    }

    // TODO: unittest
    @Override
    public void merge(Field other) {
        int otherBoardCount = other.getBoardCount();
        assert 0 < otherBoardCount && otherBoardCount <= 2 : otherBoardCount;

        xBoardLow |= other.getBoard(0);
        if (otherBoardCount == 2)
            xBoardHigh |= other.getBoard(1);
    }

    // TODO: unittest
    @Override
    public void reduce(Field other) {
        int otherBoardCount = other.getBoardCount();
        assert 0 < otherBoardCount && otherBoardCount <= 2;

        xBoardLow &= ~other.getBoard(0);
        if (otherBoardCount == 2)
            xBoardHigh &= ~other.getBoard(1);
    }

    // TODO: unittest
    @Override
    public boolean canMerge(Field other) {
        int otherBoardCount = other.getBoardCount();
        assert 0 < otherBoardCount && otherBoardCount <= 2;

        if (otherBoardCount == 1) {
            return (xBoardLow & other.getBoard(0)) == 0L && xBoardHigh == 0L;
        } else {
            return (xBoardLow & other.getBoard(0)) == 0L && (xBoardHigh & other.getBoard(1)) == 0L;
        }
    }

    // TODO: unittest
    @Override
    public int getUpperYWith4Blocks() {
        assert Long.bitCount(xBoardLow) + Long.bitCount(xBoardHigh) == 4;
        if (xBoardLow != 0L) {
            if (xBoardHigh != 0L) {
                // 何ビットかxBoardHighにある
                // xBoardHighを下から順にオフする
                long prevBoard = xBoardHigh;
                long board = xBoardHigh & (xBoardHigh - 1);
                while (board != 0L) {
                    prevBoard = board;
                    board = board & (board - 1);
                }
                return BitOperators.bitToY(prevBoard) + 6;
            } else {
                // すべてxBoardLowにある
                // xBoardLowを下から順に3bit分、オフする
                long board = xBoardLow & (xBoardLow - 1);
                board = board & (board - 1);
                board = board & (board - 1);
                return BitOperators.bitToY(board);
            }
        } else {
            // すべてxBoardHighにある
            // xBoardHighを下から順に3bit分、オフする
            long board = xBoardHigh & (xBoardHigh - 1);
            board = board & (board - 1);
            board = board & (board - 1);
            return BitOperators.bitToY(board) + 6;
        }
    }

    // TODO: unittest
    @Override
    public int getLowerY() {
        if (xBoardLow != 0L) {
            long lowerBit = xBoardLow & (-xBoardLow);
            return BitOperators.bitToY(lowerBit);
        } else {
            long lowerBit = xBoardHigh & (-xBoardHigh);
            return BitOperators.bitToY(lowerBit) + 6;
        }
    }

    // TODO: unittest
    @Override
    public void invert(int maxHeight) {
        if (maxHeight < 6) {
            xBoardLow = ~xBoardLow & BitOperators.getRowMaskBelowY(maxHeight);
            xBoardHigh = 0L;
        } else {
            xBoardLow = ~xBoardLow & 0xfffffffffffffffL;
            xBoardHigh = ~xBoardHigh & BitOperators.getRowMaskBelowY(maxHeight - 6);
        }
    }

    // TODO: unittest
    @Override
    public void slideLeft(int slide) {
        long mask = BitOperators.getColumnMaskRightX(slide);
        xBoardLow = (xBoardLow & mask) >> slide;
        xBoardHigh = (xBoardHigh & mask) >> slide;
    }

    @Override
    public Field fix() {
        if (this.xBoardHigh != 0L)
            return new FrozenMiddleField(this);
        return new FrozenSmallField(xBoardLow);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        if (getClass() == o.getClass()) {
            MiddleField that = (MiddleField) o;
            return xBoardLow == that.xBoardLow && xBoardHigh == that.xBoardHigh;
        } else if (o instanceof FrozenMiddleField) {
            FrozenMiddleField that = (FrozenMiddleField) o;
            return xBoardLow == that.getXBoardLow() && xBoardHigh == that.getXBoardHigh();
        } else if (o instanceof SmallField) {
            SmallField that = (SmallField) o;
            return xBoardHigh == 0L && xBoardLow == that.getBoard(0);
        } else if (o instanceof Field) {
            Field that = (Field) o;
            return FieldComparator.compareField(this, that) == 0;
        }

        return false;
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("this is mutable object");
    }

    @Override
    public int compareTo(Field o) {
        return FieldComparator.compareField(this, o);
    }

    @Override
    public String toString() {
        return String.format("MiddleField{low=%d, high=%d}", xBoardLow, xBoardHigh);
    }
}
