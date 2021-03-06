package searcher.common.validator;

import core.field.Field;
import core.mino.Block;
import common.datastore.action.Action;

import java.util.List;

// TODO: unittest
public class PathFullValidator implements FullValidator {
    public static PathFullValidator createWithoutHold(List<Field> expectedField, Validator validator) {
        return new PathFullValidator(expectedField, validator);
    }

    private final List<Field> expectedField;
    private final Validator validator;

    // expectedField: depthの探索後にできたフィールド
    // expectedBlock: depthの探索時に操作したミノ
    // expectedAction: depthの探索時に行った操作
    private PathFullValidator(List<Field> expectedField, Validator validator) {
        this.expectedField = expectedField;
        this.validator = validator;
    }

    @Override
    public boolean satisfies(Field currentField, Field nextField, int maxY, Block block, Action action, int depth) {
        assert depth < expectedField.size();

        // expect以外の操作で同じ地形になったら探索途中でも結果に登録
        boolean checks = checks(nextField, depth);
        if (!checks)
            return false;  // 地形が違う場合

        // 本来のパスと同じか判定
        // 全く同じなら結果としては不適切（探索を続ける）
        return 0 < depth && depth < expectedField.size() - 1 && !checks(currentField, depth - 1);
    }

    private boolean checks(Field field, int depth) {
        Field expectField = expectedField.get(depth);
        int expectBoardCount = expectField.getBoardCount();
        int fieldBoardCount = field.getBoardCount();
        if (expectBoardCount == fieldBoardCount) {
            for (int index = 0; index < expectBoardCount; index++)
                if (expectField.getBoard(index) != field.getBoard(index))
                    return false;
        } else if (expectBoardCount < fieldBoardCount) {
            int index = 0;
            for (; index < expectBoardCount; index++) {
                if (expectField.getBoard(index) != field.getBoard(index))
                    return false;
            }
            for (; index < fieldBoardCount; index++) {
                if (field.getBoard(index) != 0L)
                    return false;
            }
        } else if (fieldBoardCount < expectBoardCount) {
            int index = 0;
            for (; index < fieldBoardCount; index++) {
                if (expectField.getBoard(index) != field.getBoard(index))
                    return false;
            }
            for (; index < expectBoardCount; index++) {
                if (expectField.getBoard(index) != 0L)
                    return false;
            }
        }

        return true;
    }

    @Override
    public boolean validate(Field currentField, Field nextField, int maxClearLine, Block block, Action action, int depth) {
        return validator.validate(nextField, maxClearLine);
    }
}
