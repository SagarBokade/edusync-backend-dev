package com.project.edusync.common.exception.finance;

import com.project.edusync.common.exception.EdusyncException;

public class PaymentNotFoundException extends EdusyncException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
