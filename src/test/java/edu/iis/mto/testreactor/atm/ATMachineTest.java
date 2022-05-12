package edu.iis.mto.testreactor.atm;


import edu.iis.mto.testreactor.atm.bank.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ATMachineTest {

    @Mock Bank bank;

    private ATMachine atm;
    private final Currency irrelevantCurrency = Currency.getInstance("PLN");

    @BeforeEach
    void setUp() {
        atm = new ATMachine(bank, irrelevantCurrency);
    }

    @Test
    void constructorNullParameters() {
        assertThrows(NullPointerException.class, () -> new ATMachine(null, irrelevantCurrency));
        assertThrows(NullPointerException.class, () -> new ATMachine(bank, null));
    }

}
