package searcher.pack.separable_mino;

import common.datastore.OperationWithKey;
import common.datastore.SimpleOperationWithKey;
import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import core.field.Field;
import core.mino.Mino;
import searcher.pack.separable_mino.mask.MinoMask;
import searcher.pack.separable_mino.mask.MinoMaskFactory;

public class SeparableMino {
    public static SeparableMino create(Mino mino, long deleteKey, long usingKey, int x, int lowerY, int upperY, int fieldHeight) {
        assert 0 <= lowerY && upperY <= 10 : lowerY;

        MinoMask minoMask = MinoMaskFactory.create(fieldHeight, mino, lowerY - mino.getMinY(), deleteKey);
        Field mask = minoMask.getMinoMask(x);

        ColumnSmallField field = new ColumnSmallField();
        for (int ny = lowerY; ny <= upperY; ny++) {
            for (int nx = x + mino.getMinX(); nx <= x + mino.getMaxX(); nx++) {
                if (!mask.isEmpty(nx, ny))
                    field.setBlock(nx, ny, fieldHeight);
            }
        }

        return new SeparableMino(mino, field, x, lowerY, deleteKey, usingKey);
    }

    private final Mino mino;
    private final ColumnField field;
    private final int x;
    private final int lowerY;
    private final long deleteKey;
    private final long usingKey;
    private final OperationWithKey operation;

    private SeparableMino(Mino mino, ColumnField field, int x, int lowerY, long deleteKey, long usingKey) {
        this.mino = mino;
        this.field = field;
        this.x = x;
        this.lowerY = lowerY;
        this.deleteKey = deleteKey;
        this.usingKey = usingKey;
        this.operation = new SimpleOperationWithKey(mino, x, deleteKey, usingKey, lowerY);
    }

    public Mino getMino() {
        return mino;
    }

    public int getLowerY() {
        return lowerY;
    }

    public long getDeleteKey() {
        return deleteKey;
    }

    public ColumnField getField() {
        return field;
    }

    public int getX() {
        return x;
    }

    public long getUsingKey() {
        return usingKey;
    }

    public OperationWithKey toOperation() {
        return operation;
    }
}
