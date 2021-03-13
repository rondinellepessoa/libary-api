package com.cursotddrsilva.libraryapi.model.repository;

import com.cursotddrsilva.libraryapi.model.entity.Book;
import com.cursotddrsilva.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static com.cursotddrsilva.libraryapi.model.repository.BookRepositoryTest.createNewBook;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private LoanRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve verificar se existe emprestimo nao devolvido para o livro")
    public void existsByBookAndNotReturned(){
        //cenario
        Loan loan = createAndPersistLoan(LocalDate.now());
        Book book = loan.getBook();

        //execucao
        boolean exists = this.repository.existsByBookAndNotReturned(book);

        //verificacao
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve buscar emprestimo pelo isbn ou customer do libro")
    public void findByBookIsbnOrCustomer(){
        //cenario
        Loan loan = this.createAndPersistLoan(LocalDate.now());

        //execucao
        Page<Loan> result = this.repository.findByBookIsbnOrCustomer("123", "Fulano", PageRequest.of(0, 10));

        //verificacao
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve obter emprestimos cuja data emprestimo for menor ou igual a tres dias atras e nao retornados")
    public void findByLoanDateLessThanAndNotReturned(){
        //cenario
        Loan loan = this.createAndPersistLoan(LocalDate.now().minusDays(5));

        //execucao
        List<Loan> result = this.repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        //verificacao
        assertThat(result).hasSize(1).contains(loan);
    }

    @Test
    @DisplayName("Deve retornar vazio quando nao ouver emprestimos atrasados.")
    public void notFindByLoanDateLessThanAndNotReturned(){
        //cenario
        Loan loan = this.createAndPersistLoan(LocalDate.now());

        //execucao
        List<Loan> result = this.repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        //verificacao
        assertThat(result).isEmpty();
    }

    public Loan createAndPersistLoan(LocalDate loanDate){
        Book book = createNewBook("123");
        this.entityManager.persist(book);

        //execucao
        Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(loanDate).build();
        this.entityManager.persist(loan);

        return  loan;
    }
}
