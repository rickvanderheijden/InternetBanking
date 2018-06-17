package unittest;

import com.ark.BankTransaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestBankTransaction {
    private static final long Amount = 2000;
    private static final String Description = "Description";
    private static final String BankAccountTo = "ABNA0123456789";
    private static final String BankAccountFrom = "RABO0123456789";

    private BankTransaction transaction;

    @Before
    public void setUp() {
        transaction = new BankTransaction(Amount, Description, BankAccountFrom, BankAccountTo);
    }

    @After
    public void tearDown() {

    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorAmountZero() {
        transaction = new BankTransaction(0, Description, BankAccountFrom, BankAccountTo);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorAmountNegative() {
        transaction = new BankTransaction(-200, Description, BankAccountFrom, BankAccountTo);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorAmountTooHigh() {
        transaction = new BankTransaction(Long.MAX_VALUE + 200, Description, BankAccountFrom, BankAccountTo);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorDescriptionNull() {
        transaction = new BankTransaction(Amount, null, BankAccountFrom, BankAccountTo);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorDescriptionEmpty() {
        transaction = new BankTransaction(Amount, "", BankAccountFrom, BankAccountTo);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorBankAccountFromNull() {
        transaction = new BankTransaction(Amount, Description, null, BankAccountTo);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorBankAccountFromEmpty() {
        transaction = new BankTransaction(Amount, "", "", BankAccountTo);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorBankAccountToNull() {
        transaction = new BankTransaction(Amount, Description, BankAccountFrom, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorBankAccountToEmpty() {
        transaction = new BankTransaction(Amount, "", BankAccountFrom, "");
    }

    @Test
    public void testSetAmountZero() {
        testSetAmount(0, false);
    }

    @Test
    public void testSetAmountNegative() {
        testSetAmount(-20, false);
    }

    @Test
    public void testSetAmountTooHigh() {
        testSetAmount(Long.MAX_VALUE + 200, false);
    }

    @Test
    public void testSetAmountValid() {
        testSetAmount(Amount, true);
    }

    @Test
    public void testSetDescriptionNull() {
        testSetDescription(null, false);
    }

    @Test
    public void testSetDescriptionEmpty() {
        testSetDescription("", false);
    }

    @Test
    public void testSetDescriptionValid() {
        testSetDescription(Description, true);
    }

    @Test
    public void testSetAccountFromNull() {
        testSetAccountFrom(null, false);
    }

    @Test
    public void testSetAccountFromEmpty() {
        testSetAccountFrom("", false);
    }

    @Test
    public void testSetAccountFromValid() {
        testSetAccountFrom(BankAccountFrom, true);
    }

    @Test
    public void testSetAccountToNull() {
        testSetAccountTo(null, false);
    }

    @Test
    public void testSetAccountToEmpty() {
        testSetAccountTo("", false);
    }

    @Test
    public void testSetAccountToValid() {
        testSetAccountTo(BankAccountTo, true);
    }

    private void testSetAmount(long amount, boolean expectedResult) {
        boolean result = transaction.setAmount(amount);
        assertEquals(expectedResult, result);
    }

    private void testSetDescription(String description, boolean expectedResult) {
        boolean result = transaction.setDescription(description);
        assertEquals(expectedResult, result);
    }

    private void testSetAccountTo(String accountTo, boolean expectedResult) {
        boolean result = transaction.setAccountTo(accountTo);
        assertEquals(expectedResult, result);
    }

    private void testSetAccountFrom(String accountFrom, boolean expectedResult) {
        boolean result = transaction.setAccountFrom(accountFrom);
        assertEquals(expectedResult, result);
    }
}
