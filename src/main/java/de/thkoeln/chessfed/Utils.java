package de.thkoeln.chessfed;

import java.util.function.Function;

public class Utils {
    
    public static <I, O> O nullableAction(I operand, Function<I, O> operation) {
        if (operand == null) {
            return null;
        } else {
            return operation.apply(operand);
        }
    }

}
