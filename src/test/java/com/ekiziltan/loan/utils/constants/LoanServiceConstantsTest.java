package com.ekiziltan.loan.utils.constants;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class LoanServiceConstantsTest {

    @Test
    void testConstantsValues() {
        assertEquals("Customer is not found with given customerId: ",
                LoanServiceConstants.ERROR_CUSTOMER_NOT_FOUND,
                "ERROR_CUSTOMER_NOT_FOUND does not match the expected value.");

        assertEquals("There is an active loan application for this customer.",
                LoanServiceConstants.ERROR_ACTIVE_LOAN_APPLICATION,
                "ERROR_ACTIVE_LOAN_APPLICATION does not match the expected value.");

        assertEquals("Active loan application lock not found.",
                LoanServiceConstants.ERROR_ACTIVE_LOCK_NOT_FOUND,
                "ERROR_ACTIVE_LOCK_NOT_FOUND does not match the expected value.");

        assertEquals("There is an active installment payment for this loan.",
                LoanServiceConstants.ERROR_ACTIVE_INSTALLMENT_PAYMENT,
                "ERROR_ACTIVE_INSTALLMENT_PAYMENT does not match the expected value.");
    }

    @Test
    void testPrivateConstructor() {
        Constructor<LoanServiceConstants> constructor = null;
        try {
            constructor = LoanServiceConstants.class.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            fail("LoanServiceConstants should have a private no-arg constructor.");
        }


        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()),
                "LoanServiceConstants constructor is not private.");


        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            fail("Instantiation should have thrown an exception.");
        } catch (InvocationTargetException e) {
            // Check if the underlying exception is an UnsupportedOperationException
            assertTrue(e.getCause() instanceof UnsupportedOperationException,
                    "Expected UnsupportedOperationException, but got: " + e.getCause());
        } catch (InstantiationException | IllegalAccessException e) {

            fail("Unexpected exception type: " + e);
        }
    }
}
