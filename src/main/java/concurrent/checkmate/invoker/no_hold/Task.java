package concurrent.checkmate.invoker.no_hold;

import core.action.candidate.Candidate;
import common.datastore.Pair;
import core.mino.Block;
import common.datastore.pieces.NoSafeListPieces;
import searcher.checkmate.Checkmate;
import common.datastore.Result;
import common.datastore.action.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

class Task implements Callable<List<Pair<List<Block>, List<Result>>>> {
    private final Obj obj;
    private final List<NoSafeListPieces> targets;

    Task(Obj obj, List<NoSafeListPieces> targets) {
        this.obj = obj;
        this.targets = targets;
    }

    @Override
    public List<Pair<List<Block>, List<Result>>> call() throws Exception {
        Checkmate<Action> checkmate = obj.checkmateThreadLocal.get();
        Candidate<Action> candidate = obj.candidateThreadLocal.get();

        // 探索
        List<Pair<List<Block>, List<Result>>> allResults = new ArrayList<>();
        for (NoSafeListPieces piece : targets) {
            List<Block> blocks = piece.getBlocks();
            List<Result> results = checkmate.search(obj.field, blocks, candidate, obj.maxClearLine, obj.maxDepth);
            allResults.add(new Pair<>(blocks, results));
        }

        return allResults;
    }
}
