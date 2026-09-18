package com.thecommitcrew.domain.validator;
import com.thecommitcrew.domain.enums.AccountStatus;

/**
 * Interface for validating account status before performing debit or credit operations.
 */
public interface AccountStatusValidator {
    /**
     * Validates if the account with the given status can be debited.
     *
     * @param status the current account status
     * @throws IllegalStateException if the account cannot be debited
     */
    void validateCanDebit(AccountStatus status);

    /**
     * Validates if the account with the given status can be credited.
     *
     * @param status the current account status
     * @throws IllegalStateException if the account cannot be credited
     */
    void validateCanCredit(AccountStatus status);
}
