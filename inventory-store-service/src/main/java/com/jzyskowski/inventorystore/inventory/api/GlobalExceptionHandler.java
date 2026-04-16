package com.jzyskowski.inventorystore.inventory.api;

import com.jzyskowski.inventorystore.inventory.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OfferNotFoundException.class)
    public ProblemDetail handleOfferNotFound(OfferNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(OfferNotActiveException.class)
    public ProblemDetail handleOfferNotActive(OfferNotActiveException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(PurchaseLimitReachedException.class)
    public ProblemDetail handlePurchaseLimit(PurchaseLimitReachedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InsufficientFundsForPurchaseException.class)
    public ProblemDetail handleInsufficientFunds(InsufficientFundsForPurchaseException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.PAYMENT_REQUIRED, ex.getMessage());
    }

    @ExceptionHandler(PurchaseFailedException.class)
    public ProblemDetail handlePurchaseFailed(PurchaseFailedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
    }

    @ExceptionHandler(InventoryNotFoundException.class)
    public ProblemDetail handleInventoryNotFound(InventoryNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(PurchaseNotFoundException.class)
    public ProblemDetail handlePurchaseNotFound(PurchaseNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }
}