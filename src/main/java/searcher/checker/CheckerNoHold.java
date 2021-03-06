package searcher.checker;

import core.action.candidate.Candidate;
import core.field.Field;
import core.mino.Block;
import core.mino.MinoFactory;
import common.datastore.Result;
import searcher.core.SimpleSearcherCore;
import common.datastore.action.Action;
import common.datastore.order.DepthOrder;
import common.datastore.order.Order;
import searcher.common.validator.Validator;

import java.util.List;

public class CheckerNoHold<T extends Action> implements Checker<T> {
    private final CheckerDataPool dataPool;
    private final SimpleSearcherCore<T> searcherCore;

    public CheckerNoHold(MinoFactory minoFactory, Validator validator) {
        this.dataPool = new CheckerDataPool();
        this.searcherCore = new SimpleSearcherCore<>(minoFactory, validator, dataPool);
    }

    @Override
    public boolean check(Field initField, List<Block> pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        Block[] blocks = new Block[pieces.size()];
        return check(initField, pieces.toArray(blocks), candidate, maxClearLine, maxDepth);
    }

    @Override
    public boolean check(Field initField, Block[] pieces, Candidate<T> candidate, int maxClearLine, int maxDepth) {
        dataPool.initFirst();
        dataPool.addOrder(new DepthOrder(initField, null, maxClearLine, maxDepth));

        while (!dataPool.getNexts().isEmpty() && dataPool.getResults().isEmpty()) {
            Order order = dataPool.getNexts().pollLast();
            int depth = order.getHistory().getNextIndex();
            boolean isLast = depth == maxDepth;

            assert depth < pieces.length;
            searcherCore.stepWithNextNoHold(candidate, pieces[depth], order, isLast);
        }

        return !dataPool.getResults().isEmpty();
    }

    public Result getResult() {
        return dataPool.getResults().get(0);
    }
}
