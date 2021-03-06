package _implements.parity_based_pack.step2;

import core.field.Field;
import core.mino.Mino;
import _implements.parity_based_pack.ParityField;
import searcher.pack.separable_mino.mask.MinoMask;

public class FullLimitedMino implements Comparable<FullLimitedMino> {
    public static FullLimitedMino create(Mino mino, PositionLimit positionLimit, DeleteKey deleteKey) {
        return new FullLimitedMino(mino, positionLimit, deleteKey);
    }

    public static final int FIELD_WIDTH = 10;

    private final Mino mino;
    private final PositionLimit positionLimit;
    private final DeleteKey deleteKey;
    private final int[] xs;
    private final int[] parity;

    private FullLimitedMino(Mino mino, PositionLimit positionLimit, DeleteKey deleteKey) {
        this.mino = mino;
        this.positionLimit = positionLimit;
        this.deleteKey = deleteKey;
        this.xs = createX(mino, positionLimit);
        this.parity = createParity(deleteKey, positionLimit);
    }

    private int[] createX(Mino mino, PositionLimit positionLimit) {
        int minX = -mino.getMinX();
        int maxX = FIELD_WIDTH - mino.getMaxX();
        switch (positionLimit) {
            case OddX:
                return createOdd(minX, maxX);
            case EvenX:
                return createEven(minX, maxX);
        }
        throw new IllegalStateException("No reachable");
    }

    private int[] createParity(DeleteKey deleteKey, PositionLimit positionLimit) {
        switch (positionLimit) {
            case OddX:
                Field oddField = deleteKey.getMinoMask().getMinoMask(6);
                ParityField oddParityField = new ParityField(oddField);
                return new int[]{oddParityField.calculateOddParity(), oddParityField.calculateEvenParity()};
            case EvenX:
                Field evenField = deleteKey.getMinoMask().getMinoMask(5);
                ParityField evenParityField = new ParityField(evenField);
                return new int[]{evenParityField.calculateOddParity(), evenParityField.calculateEvenParity()};
        }
        throw new IllegalStateException("No reachable");
    }

    private int[] createOdd(int minX, int maxX) {
        minX = minX % 2 == 0 ? minX : minX + 1;
        int[] ints = new int[(maxX - minX + 1) / 2];
        for (int index = 0; index < ints.length; index++)
            ints[index] = minX + index * 2;
        return ints;
    }

    private int[] createEven(int minX, int maxX) {
        minX = minX % 2 == 1 ? minX : minX + 1;
        int[] ints = new int[(maxX - minX + 1) / 2];
        for (int index = 0; index < ints.length; index++)
            ints[index] = minX + index * 2;
        return ints;
    }

    public Mino getMino() {
        return mino;
    }

    public int[] getXs() {
        return xs;
    }

    public MinoMask getMinoMask() {
        return deleteKey.getMinoMask();
    }

    public int[][] getBlockCountEachLines() {
        return deleteKey.getBlockCountEachLines();
    }

    public int getLowerY() {
        return deleteKey.getLowerY();
    }

    public long getDeleteKey() {
        return deleteKey.getNeedKey();
    }

    public long getUsingKey() {
        return deleteKey.getUsingKey();
    }

    public int[] getParity() {
        return parity;
    }

    @Override
    public String toString() {
        return "FullLimitedMino{" +
                "mino=" + mino.getBlock() + "-" + mino.getRotate() + ":" + deleteKey +
                ",pos=" + positionLimit +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    PositionLimit getPositionLimit() {
        return positionLimit;
    }

    @Override
    public int compareTo(FullLimitedMino o) {
        int block = mino.getBlock().compareTo(o.mino.getBlock());
        if (block != 0)
            return block;

        int rotate = mino.getRotate().compareTo(o.mino.getRotate());
        if (rotate != 0)
            return rotate;

        int position = positionLimit.compareTo(o.positionLimit);
        if (position != 0)
            return position;

        int lowerY = Integer.compare(deleteKey.getLowerY(), o.deleteKey.getLowerY());
        if (lowerY != 0)
            return lowerY;

        return Long.compare(deleteKey.getNeedKey(), o.deleteKey.getNeedKey());
    }
}
