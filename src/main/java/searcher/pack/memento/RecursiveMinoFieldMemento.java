package searcher.pack.memento;

import common.datastore.OperationWithKey;
import searcher.pack.mino_field.MinoField;
import searcher.pack.SlideXOperationWithKey;

import java.util.stream.Stream;

public class RecursiveMinoFieldMemento implements MinoFieldMemento {
    private final MinoField current;
    private final RecursiveMinoFieldMemento parent;
    private final Boolean isConcat;  // null=0, false=1, true=2<=
    private final int size;

    public RecursiveMinoFieldMemento(MinoField current, RecursiveMinoFieldMemento parent, Boolean isConcat, int size) {
        this.isConcat = isConcat;
        this.size = size;
        this.current = current;
        this.parent = parent;
    }

    @Override
    public MinoFieldMemento concat(MinoField minoField) {
        if (minoField == null) {
            return new RecursiveMinoFieldMemento(null, this, isConcat, size + 1);
        } else {
            if (isConcat == null)
                return new RecursiveMinoFieldMemento(minoField, this, false, size + 1);
            else
                return new RecursiveMinoFieldMemento(minoField, this, true, size + 1);
        }
    }

    @Override
    public MinoFieldMemento skip() {
        return concat(null);
    }

    @Override
    public long getSumBlockCounter() {
        long sum = 0L;
        RecursiveMinoFieldMemento target = this;
        do {
            if (target.current != null)
                sum += target.current.getBlockCounter().getCounter();
            target = target.parent;
        } while (target != null);
        return sum;
    }

    @Override
    public Stream<OperationWithKey> getRawOperationsStream() {
        Stream<OperationWithKey> operations = Stream.empty();
        RecursiveMinoFieldMemento target = this;
        do {
            if (target.current != null)
                operations = Stream.concat(operations, target.current.getOperationsStream());
            target = target.parent;
        } while (target != null);
        return operations;
    }

    @Override
    public boolean isConcat() {
        return isConcat != null ? isConcat : false;
    }

    @Override
    public Stream<OperationWithKey> getOperationsStream(int width) {
        Stream<OperationWithKey> operations = Stream.empty();
        RecursiveMinoFieldMemento target = this;
        int depth = size - 1;
        do {
            if (target.current != null)
                operations = Stream.concat(operations, addSlideX(target.current.getOperationsStream(), width * depth));
            target = target.parent;
            depth--;
        } while (target != null);
        return operations;
    }
    
    private Stream<OperationWithKey> addSlideX(Stream<OperationWithKey> operations, int slideX) {
        return operations.map(operationWithKey -> toSlideWrapper(operationWithKey, slideX));
    }

    private OperationWithKey toSlideWrapper(OperationWithKey operationWithKey, int slideX) {
        return new SlideXOperationWithKey(operationWithKey, slideX);
    }
}
