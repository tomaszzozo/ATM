package edu.iis.mto.testreactor.atm;


import edu.iis.mto.testreactor.atm.bank.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ATMachineTest {

    @Mock Bank bank;

    private ATMachine atm;
    private final Currency standardCurrency = Currency.getInstance("PLN");

    @BeforeEach
    void setUp() {
        atm = new ATMachine(bank, standardCurrency);
    }

    @Test
    void constructorNullParameters() {
        assertThrows(NullPointerException.class, () -> new ATMachine(null, standardCurrency));
        assertThrows(NullPointerException.class, () -> new ATMachine(bank, null));
    }

    @Test
    void setDepositNullParameter() {
        assertThrows(NullPointerException.class, () -> atm.setDeposit(null));
    }

    @Test
    void getCurrentDepositTest() {
        ArrayList<BanknotesPack> banknotes = new ArrayList<>(Collections.singletonList(BanknotesPack.create(10, Banknote.PL_10)));
        MoneyDeposit deposit = MoneyDeposit.create(standardCurrency, banknotes);
        atm.setDeposit(deposit);

        MoneyDeposit result = atm.getCurrentDeposit();
        assertEquals(standardCurrency, result.getCurrency());
        assertEquals(banknotes.get(0), result.getBanknotes().get(0));
    }

}
