package unittest;

import com.ark.BankAccount;
import com.ark.Customer;
import com.ark.bank.IBankAccount;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Rick van der Heijden
 */
public class TestBankAccount {

    private static final String BankAccountNumber = "BANK0123456789";
    private static final Customer Owner = new Customer("Name", "Residence", "Password");
    private IBankAccount bankAccount;

    @Before
    public void setUp() {
        bankAccount = new BankAccount(Owner, BankAccountNumber);
    }

    @After
    public void tearDown() {
        bankAccount = null;
    }

    @Test
    public void testGetOwner() {
        Customer result = bankAccount.getOwner();
        assertNotNull(result);
    }

    @Test
    public void testGetNumber() {
        String result = bankAccount.getNumber();
        assertNotNull(result);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorOwnerNull() {
        bankAccount = new BankAccount(null, BankAccountNumber);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorBankAccountNumberNull() {
        bankAccount = new BankAccount(Owner, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructorBankAccountNumberEmpty() {
        bankAccount = new BankAccount(Owner, "");
    }

    @Test
    public void testGetCreditLimitDefaultValue() {
        assertEquals(10000, bankAccount.getCreditLimit());
    }

    @Test
    public void testSetCreditLimitNegativeValue() {
        testSetCreditLimit(-100, false);
    }

    @Test
    public void testSetCreditLimitPositiveValue() {
        testSetCreditLimit(1, true);
    }

    @Test
    public void testSetCreditLimitZeroValue() {
        testSetCreditLimit(0, true);
    }

    @Test
    public void testIncreaseBalanceNegativeValue() {
        testIncreaseBalance(-10, false);
    }

    @Test
    public void testIncreaseBalancePositiveValue() {
        testIncreaseBalance(900000000, true);
    }

    @Test
    public void testIncreaseBalanceZeroValue() {
        testIncreaseBalance(0, false);
    }

    @Test
    public void testDecreaseBalanceNegativeValue() {
        testDecreaseBalance(-10, false);
    }

    @Test
    public void testDecreaseBalancePositiveValue() {
        testDecreaseBalance(2000, true);
    }

    @Test
    public void testDecreaseBalanceAboceCreditLimit() {
        testDecreaseBalance(900000000, false);
    }

    @Test
    public void testDecreaseBalanceZeroValue() {
        testDecreaseBalance(0, false);
    }

    @Test
    public void testIncreaseDecreaseBalanceMultiThreading() throws InterruptedException {
        bankAccount.setCreditLimit(100000000);

        Thread threadOne = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                bankAccount.increaseBalance(1000);
            }
        });

        Thread threadTwo = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                bankAccount.decreaseBalance(1);
            }
        });

        Thread threadThree = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                bankAccount.decreaseBalance(4000);
            }
        });

        Thread threadFour = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                bankAccount.increaseBalance(5);
            }
        });

        threadOne.start();
        threadTwo.start();
        threadThree.start();
        threadFour.start();

        threadOne.join();
        threadTwo.join();
        threadThree.join();
        threadFour.join();

        assertEquals(-2996000, bankAccount.getBalance());
    }

    private void testSetCreditLimit(long creditLimit, boolean expectedResult) {
        boolean result = bankAccount.setCreditLimit(creditLimit);
        assertEquals(expectedResult, result);

        if (expectedResult) {
            assertEquals(creditLimit, bankAccount.getCreditLimit());
        }
    }

    private void testIncreaseBalance(long amount, boolean expectedResult) {
        long balance = bankAccount.getBalance();
        boolean result = bankAccount.increaseBalance(amount);
        assertEquals(expectedResult, result);
        assertEquals(expectedResult ? balance + amount : balance, bankAccount.getBalance());
    }

    private void testDecreaseBalance(long amount, boolean expectedResult) {
        long balance = bankAccount.getBalance();
        boolean result = bankAccount.decreaseBalance(amount);
        assertEquals(expectedResult, result);
        assertEquals(expectedResult ? balance - amount : balance, bankAccount.getBalance());
    }
}
