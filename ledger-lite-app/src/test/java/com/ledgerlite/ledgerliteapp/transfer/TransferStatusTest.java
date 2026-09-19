package com.ledgerlite.ledgerliteapp.transfer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TransferStatusTest {

    @Test
    void pendingCanTransitionToCompleted(){
        assertThat(TransferStatus.PENDING.canTransitionTo(TransferStatus.COMPLETED)).isTrue();
    }

    @Test
    void pendingCanTransitionToFailed() {
        assertThat(TransferStatus.PENDING.canTransitionTo(TransferStatus.FAILED)).isTrue();
    }

    @Test
    void completedCannotTransitionToPending() {
        assertThat(TransferStatus.COMPLETED.canTransitionTo(TransferStatus.PENDING)).isFalse();
    }

    @Test
    void completedCannotTransitionToFailed() {
        assertThat(TransferStatus.COMPLETED.canTransitionTo(TransferStatus.FAILED)).isFalse();
    }

    @Test
    void failedCannotTransitionToCompleted() {
        assertThat(TransferStatus.FAILED.canTransitionTo(TransferStatus.COMPLETED)).isFalse();
    }

    @Test
    void statusCannotTransitionToItself() {
        assertThat(TransferStatus.PENDING.canTransitionTo(TransferStatus.PENDING)).isFalse();
        assertThat(TransferStatus.COMPLETED.canTransitionTo(TransferStatus.COMPLETED)).isFalse();
    }
}
