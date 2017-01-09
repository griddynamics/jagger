package com.griddynamics.jagger.user.test.configurations.load.auxiliary;

/**
 * The number of transactions (request + response) per second Jagger shall perform.
 *
 */
public final class TransactionsPerSecond {
    private final long transactionsPerSecond;

    TransactionsPerSecond(long transactionsPerSecond) {
        if (transactionsPerSecond <= 0) {
            throw new IllegalArgumentException(
                    String.format("Transactions per second must be > 0. Provided value is %s", transactionsPerSecond));
        }
        this.transactionsPerSecond = transactionsPerSecond;
    }

    public long value() {
        return transactionsPerSecond;
    }

    public static TransactionsPerSecond of(long transactionsPerSecond) {
        return new TransactionsPerSecond(transactionsPerSecond);
    }
}
