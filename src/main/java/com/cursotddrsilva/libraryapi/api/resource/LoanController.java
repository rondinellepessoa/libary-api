package com.cursotddrsilva.libraryapi.api.resource;

import com.cursotddrsilva.libraryapi.api.dto.BookDTO;
import com.cursotddrsilva.libraryapi.api.dto.LoanDto;
import com.cursotddrsilva.libraryapi.api.dto.LoanFilterDTO;
import com.cursotddrsilva.libraryapi.api.dto.ReturnedLoanDTO;
import com.cursotddrsilva.libraryapi.model.entity.Book;
import com.cursotddrsilva.libraryapi.model.entity.Loan;
import com.cursotddrsilva.libraryapi.service.BookService;
import com.cursotddrsilva.libraryapi.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/loans")
@RequiredArgsConstructor
@Api("Loan API")
public class LoanController {

    private final LoanService service;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creates a loan")
    public Long create(@RequestBody LoanDto dto){
        Book book = bookService
                .getBookByIsbn(dto.getIsbn())
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn."));
        Loan entity = Loan.builder()
                .book(book)
                .customer(dto.getCustomer())
                .loanDate(LocalDate.now())
                .build();

        entity = service.save(entity);

        return entity.getId();
    }

    @PatchMapping("{id}")
    @ApiOperation("Find the loan returned.")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto){
        Loan loan = this.service.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(dto.getReturned());
        this.service.update(loan);
    }

    @GetMapping
    @ApiOperation("Find a loan")
    public Page<LoanDto> find(LoanFilterDTO dto, Pageable pageable){
        Page<Loan> result = this.service.find(dto, pageable);
        List<LoanDto> loans = result
                .getContent()
                .stream()
                .map(entity -> {
                    Book book = entity.getBook();
                    BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
                    LoanDto loanDto = modelMapper.map(entity, LoanDto.class);
                    loanDto.setBookDTO(bookDTO);
                    return loanDto;
                }).collect(Collectors.toList());

        return new PageImpl<>(loans, pageable, result.getTotalElements());
    }

}
