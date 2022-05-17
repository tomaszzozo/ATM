package edu.iis.mto.testreactor.atm;


import edu.iis.mto.testreactor.atm.bank.AuthorizationException;
import edu.iis.mto.testreactor.atm.bank.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ATMachineTest {

    @Mock Bank bankMock;

    private ATMachine atm;
    private final Currency standardCurrency = Currency.getInstance("PLN");
    private final Card standardCard = Card.create("1234123412341234");
    private final Money standardMoney = new Money(10, standardCurrency);
    private final PinCode standardPin = PinCode.createPIN(1, 2, 3, 4);
    private final ArrayList<BanknotesPack> standardBanknotes =
            new ArrayList<>(Collections.singletonList(BanknotesPack.create(10, Banknote.PL_10)));
    private final MoneyDeposit standardDeposit = MoneyDeposit.create(standardCurrency, standardBanknotes);

    @BeforeEach
    void setUp() {
        atm = new ATMachine(bankMock, standardCurrency);
    }

    @Test
    void constructorNullParameters() {
        assertThrows(NullPointerException.class, () -> new ATMachine(null, standardCurrency));
        assertThrows(NullPointerException.class, () -> new ATMachine(bankMock, null));
    }

    @Test
    void setDepositNullParameter() {
        assertThrows(NullPointerException.class, () -> atm.setDeposit(null));
    }

    @Test
    void getCurrentDepositTest() {
        atm.setDeposit(standardDeposit);

        MoneyDeposit result = atm.getCurrentDeposit();
        assertEquals(standardCurrency, result.getCurrency());
        assertEquals(standardBanknotes.get(0), result.getBanknotes().get(0));
    }

    @Test
    void withdrawNullParameters() {
        assertThrows(NullPointerException.class, () -> atm.withdraw(null, standardCard, standardMoney));
        assertThrows(NullPointerException.class, () -> atm.withdraw(standardPin, null, standardMoney));
        assertThrows(NullPointerException.class, () -> atm.withdraw(standardPin, standardCard, null));
    }

    @Test
    void withdrawUnavailableCurrency() {
        Currency unavailableCurrency = Currency.getInstance("GBP");
        Money moneyInWrongCurrency = new Money(1, unavailableCurrency);

        ATMOperationException result = assertThrows(ATMOperationException.class, () -> atm.withdraw(standardPin, standardCard, moneyInWrongCurrency));
        assertEquals(ErrorCode.WRONG_CURRENCY, result.getErrorCode());
    }

    @Test
    void withdrawAuthorisationException() throws AuthorizationException {
        doThrow(AuthorizationException.class).when(bankMock).autorize(anyString(), anyString());
        ATMOperationException result = assertThrows(ATMOperationException.class, () -> atm.withdraw(standardPin, standardCard, standardMoney));
        assertEquals(ErrorCode.AHTHORIZATION, result.getErrorCode());
    }

    @Test
    void withdrawTooMuchException() {
        atm.setDeposit(standardDeposit);
        Money tooMuchMoney = new Money(1000000, standardCurrency);
        ATMOperationException result = assertThrows(ATMOperationException.class, () -> atm.withdraw(standardPin, standardCard, tooMuchMoney));
        assertEquals(ErrorCode.WRONG_AMOUNT, result.getErrorCode());
    }

    @Test
    void cantWithdrawSpecificAmountException() {
        atm.setDeposit(standardDeposit);
        Money weirdlySpecificAmountOfMoney = new Money(9, standardCurrency);
        ATMOperationException result = assertThrows(ATMOperationException.class, () -> atm.withdraw(standardPin, standardCard, weirdlySpecificAmountOfMoney));
        assertEquals(ErrorCode.WRONG_AMOUNT, result.getErrorCode());
    }
}
