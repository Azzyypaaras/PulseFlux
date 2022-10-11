package net.id.pulseflux.util;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;

import java.util.function.Function;

public class TransferHelper {

    public static void trans(Function<Transaction, Boolean> wrapper) {
        try(Transaction transaction = Transaction.openOuter()) {
            if (wrapper.apply(transaction)) {
                transaction.commit();
            } else {
                transaction.abort();
            }
        }
    }
}
