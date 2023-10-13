package book.store.service.book;

import book.store.dto.book.BookDto;
import book.store.dto.book.BookSearchParametersDto;
import book.store.dto.book.CreateBookRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.BookMapper;
import book.store.model.Book;
import book.store.repository.book.BookRepository;
import book.store.repository.book.BookSpecificationBuilder;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto book) {
        Book modelBook = bookMapper.toModel(book);
        return bookMapper.toDto(bookRepository.save(modelBook));
    }

    @Override
    public BookDto getBookById(Long id) {
        return bookMapper.toDto(bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't get book by id: " + id)));
    }

    @Override
    public List<BookDto> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void updateBookById(Long id, CreateBookRequestDto updateDto) {
        if (bookRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Can't find book by id: " + id);
        }
        Book book = bookMapper.toModel(updateDto);
        book.setId(id);
        bookRepository.save(book);
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> searchBooks(BookSearchParametersDto parametersDto) {
        Specification<Book> bookSpecification = bookSpecificationBuilder.build(parametersDto);
        return bookRepository.findAll(bookSpecification).stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }
}