package common;

import common.datastore.Operation;
import common.datastore.action.Action;
import core.mino.Block;

import java.util.Arrays;

public class OperationHistory {
    private final int[] operationNumbers;
    private final int nextIndex;

    public OperationHistory(int max) {
        this.operationNumbers = new int[max];
        this.nextIndex = 0;
    }

    private OperationHistory(int[] history, int nextIndex) {
        this.operationNumbers = history;
        this.nextIndex = nextIndex;
    }

    public OperationHistory recordAndReturnNew(Block block, Action action) {
        return recordAndReturnNew(ActionParser.parseToInt(block, action));
    }

    private OperationHistory recordAndReturnNew(int value) {
        int[] newArray = new int[operationNumbers.length];
        System.arraycopy(operationNumbers, 0, newArray, 0, nextIndex);
        newArray[nextIndex] = value;
        return new OperationHistory(newArray, nextIndex + 1);
    }

    int[] getOperationNumbers() {
        return operationNumbers;
    }

    public int getNextIndex() {
        return nextIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationHistory that = (OperationHistory) o;
        if (nextIndex != that.nextIndex) return false;
        return Arrays.equals(operationNumbers, that.operationNumbers);
    }

    @Override
    public int hashCode() {
        int result = nextIndex;
        result = 31 * result + Arrays.hashCode(operationNumbers);
        return result;
    }

    @Override
    public String toString() {
        if (operationNumbers == null || operationNumbers.length < 1)
            return "";

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < nextIndex; i++) {
            int history = operationNumbers[i];
            Operation operation = ActionParser.parseToOperation(history);
            str.append(operation).append(" / ");
        }
        return str.substring(0, str.length() - 3);
    }
}
