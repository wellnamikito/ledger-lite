package com.ledgerlite.ledgerliteapp.transfer;

import java.util.Set;

public enum TransferStatus {

    PENDING,
    COMPLETED,
    FAILED;

    private static final Set<TransferStatus> NOT_FURTHER_TRANSITIONS = Set.of(COMPLETED, FAILED);

    public boolean canTransitionTo(
            TransferStatus target
    ){
        if(this == target){
            return false;
        }
        if(NOT_FURTHER_TRANSITIONS.contains(this)){
            return false;
        }

        return this == PENDING && (target == COMPLETED || target == FAILED);
    }
}
