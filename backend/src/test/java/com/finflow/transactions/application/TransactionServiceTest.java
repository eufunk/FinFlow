package com.finflow.transactions.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.finflow.shared.Money;
import com.finflow.transactions.domain.Category;
import com.finflow.transactions.domain.CategoryNotFoundException;
import com.finflow.transactions.domain.CategoryRepository;
import com.finflow.transactions.domain.CategoryTypeMismatchException;
import com.finflow.transactions.domain.Transaction;
import com.finflow.transactions.domain.TransactionRepository;
import com.finflow.transactions.domain.TransactionType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CATEGORY_ID = UUID.randomUUID();

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Category category;

    private TransactionService service;

    @BeforeEach
    void setUp() {
        service = new TransactionService(transactionRepository, categoryRepository);
    }

    private CreateTransactionCommand expenseCommand() {
        return new CreateTransactionCommand(
                Money.of("50"), TransactionType.EXPENSE, CATEGORY_ID, LocalDate.now(), "Einkauf");
    }

    @Test
    void createsTransactionWhenCategoryTypeMatches() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(category.type()).thenReturn(TransactionType.EXPENSE);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = service.createTransaction(USER_ID, expenseCommand());

        assertThat(result.userId()).isEqualTo(USER_ID);
        assertThat(result.categoryId()).isEqualTo(CATEGORY_ID);
    }

    @Test
    void throwsWhenCategoryDoesNotExist() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createTransaction(USER_ID, expenseCommand()))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void throwsWhenCategoryTypeDoesNotMatchTransactionType() {
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(category.type()).thenReturn(TransactionType.INCOME);

        assertThatThrownBy(() -> service.createTransaction(USER_ID, expenseCommand()))
                .isInstanceOf(CategoryTypeMismatchException.class);
    }

    @Test
    void listsAllTransactionsWhenNoDateFilterGiven() {
        when(transactionRepository.findByUserId(USER_ID)).thenReturn(List.of());

        service.listTransactions(USER_ID, null, null);

        org.mockito.Mockito.verify(transactionRepository).findByUserId(USER_ID);
    }

    @Test
    void listsFilteredTransactionsWhenBothDatesGiven() {
        LocalDate from = LocalDate.now().minusDays(30);
        LocalDate to = LocalDate.now();
        when(transactionRepository.findByUserIdAndBookedAtBetween(USER_ID, from, to)).thenReturn(List.of());

        service.listTransactions(USER_ID, from, to);

        org.mockito.Mockito.verify(transactionRepository).findByUserIdAndBookedAtBetween(USER_ID, from, to);
    }

    @Test
    void rejectsOnlyFromWithoutTo() {
        assertThatThrownBy(() -> service.listTransactions(USER_ID, LocalDate.now(), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsFromAfterTo() {
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now().minusDays(1);

        assertThatThrownBy(() -> service.listTransactions(USER_ID, from, to))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
