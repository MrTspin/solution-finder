package searcher.pack.task;

import core.column_field.ColumnField;
import core.column_field.ColumnSmallField;
import searcher.pack.InOutPairField;
import searcher.pack.mino_field.MinoField;
import searcher.pack.mino_fields.MinoFields;
import searcher.pack.SizedBit;
import searcher.pack.memento.MinoFieldMemento;
import searcher.pack.memento.SolutionFilter;

import java.util.List;
import java.util.stream.Stream;

// Width=3のBasicSolutionsのためのタスク
class MinoPackingTaskWidthForWidth3 implements PackingTask {
    private static final PackingTask EMPTY_TASK = null;

    private final PackSearcher searcher;
    private final ColumnField innerField;
    private final MinoFieldMemento memento;
    private final int index;

    MinoPackingTaskWidthForWidth3(PackSearcher searcher, ColumnField innerField, MinoFieldMemento memento, int index) {
        this.searcher = searcher;
        this.innerField = innerField;
        this.memento = memento;
        this.index = index;
    }

    @Override
    public Stream<Result> compute() {
        if (innerField.getBoard(0) == searcher.getSizedBit().getFillBoard()) {
            // innerFieldが埋まっている
            List<InOutPairField> inOutPairFields = searcher.getInOutPairFields();
            if (index == searcher.getLastIndex()) {
                // 最後の計算
                ColumnField lastOuterField = inOutPairFields.get(index).getOuterField();
                MinoFieldMemento nextMemento = memento.skip();
                return searcher.getTaskResultHelper().fixResult(searcher, lastOuterField, nextMemento);
            } else {
                // 途中の計算  // 自分で計算する
                int nextIndex = index + 1;
                ColumnField nextInnerField = inOutPairFields.get(nextIndex).getInnerField();
                MinoFieldMemento nextMemento = memento.skip();
                return createTask(searcher, nextInnerField, nextMemento, nextIndex).compute();
            }
        } else {
            MinoFields minoFields = searcher.getSolutions().parse(innerField);

            // innerFieldが埋まっていない
            if (index == searcher.getLastIndex()) {
                // 最後の計算
                return minoFields.stream().parallel()
                        .flatMap(this::splitAndFixResult);
            } else {
                // 途中の計算
                return minoFields.stream().parallel()
                        .map(this::split)
                        .filter(this::isValidTask)
                        .flatMap(PackingTask::compute);
            }
        }
    }

    private PackingTask createTask(PackSearcher searcher, ColumnField innerField, MinoFieldMemento memento, int index) {
        return new MinoPackingTaskWidthForWidth3(searcher, innerField, memento, index);
    }

    private PackingTask split(MinoField minoField) {
        ColumnField outerField = searcher.getInOutPairFields().get(index).getOuterField();
        ColumnField minoOuterField = minoField.getOuterField();

        // 注目範囲外outerで重なりがないか確認
        if (outerField.canMerge(minoOuterField)) {
            // 有効なおきかた
            SizedBit sizedBit = searcher.getSizedBit();
            ColumnField mergedOuterField = outerField.freeze(sizedBit.getHeight());
            mergedOuterField.merge(minoOuterField);

            ColumnSmallField nextInnerField = new ColumnSmallField(mergedOuterField.getBoard(0) >> sizedBit.getMaxBitDigit());
            MinoFieldMemento nextMemento = memento.concat(minoField);
            return checkAndCreateTask(nextInnerField, nextMemento, index + 1);
        }

        return EMPTY_TASK;
    }

    private PackingTask checkAndCreateTask(ColumnField innerField, MinoFieldMemento memento, int index) {
        SolutionFilter solutionFilter = searcher.getSolutionFilter();
        if (solutionFilter.test(memento))
            return createTask(searcher, innerField, memento, index);
        return EMPTY_TASK;
    }

    private Stream<Result> splitAndFixResult(MinoField minoField) {
        ColumnField outerField = searcher.getInOutPairFields().get(index).getOuterField();
        ColumnField minoOuterField = minoField.getOuterField();

        // 注目範囲外outerで重なりがないか確認
        if (outerField.canMerge(minoOuterField)) {
            // 有効なおきかた
            ColumnField mergedOuterField = outerField.freeze(searcher.getSizedBit().getHeight());
            mergedOuterField.merge(minoOuterField);
            MinoFieldMemento nextMemento = memento.concat(minoField);
            return searcher.getTaskResultHelper().fixResult(searcher, mergedOuterField, nextMemento);
        }

        return Stream.empty();
    }

    private boolean isValidTask(PackingTask task) {
        return task != EMPTY_TASK;
    }
}
