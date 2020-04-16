package kostka.commentservice;

import kostka.commentservice.dto.CommentDto;
import kostka.commentservice.exception.CommentNotFoundException;
import kostka.commentservice.exception.InvalidDtoException;
import kostka.commentservice.model.Comment;
import kostka.commentservice.repository.CommentRepository;
import kostka.commentservice.service.CommentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static kostka.commentservice.CommentServiceIntegrationTest.TEST_COMMENT_TEXT;
import static kostka.commentservice.CommentServiceIntegrationTest.TEST_ID_1;
import static kostka.commentservice.CommentServiceIntegrationTest.TEST_ID_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommentServiceTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;

    @Test
    public void createCommentValidDtoTest() {
        Comment comment = new Comment(TEST_ID_1, TEST_ID_1, TEST_ID_1, TEST_COMMENT_TEXT);
        CommentDto dto = new CommentDto(TEST_ID_1, TEST_ID_1, TEST_COMMENT_TEXT);
        when(commentRepository.save(any())).thenReturn(comment);

        Comment result = commentService.createComment(dto);

        assertThat(result).isNotEqualTo(comment);
    }

    @Test
    public void createCommentInvalidDtoTest() {
        CommentDto dto = new CommentDto();

        assertThatThrownBy(() -> commentService.createComment(dto))
                .isInstanceOf(InvalidDtoException.class);
    }

    @Test
    public void getExistingCommentTest() {
        Comment comment = new Comment(TEST_ID_1, TEST_ID_1, TEST_ID_1, TEST_COMMENT_TEXT);
        when(commentRepository.findById(eq(TEST_ID_1))).thenReturn(Optional.of(comment));

        Comment result = commentService.getComment(TEST_ID_1);

        assertThat(result).isEqualTo(comment);
    }

    @Test
    public void getNonExistingCommentTest() {
        when(commentRepository.findById(eq(TEST_ID_1))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.getComment(TEST_ID_1))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    public void getCommentOfMovieTest() {
        Comment comment1 = new Comment(TEST_ID_1, TEST_ID_1, TEST_ID_1, TEST_COMMENT_TEXT);
        Comment comment2 = new Comment(TEST_ID_2, TEST_ID_1, TEST_ID_2, TEST_COMMENT_TEXT);
        List<Comment> commentList = Arrays.asList(comment1, comment2);
        when(commentRepository.findAllByMovieId(eq(TEST_ID_1))).thenReturn(commentList);

        List<Comment> resultList = commentService.getAllCommentsForMovie(TEST_ID_1);

        assertThat(resultList).contains(comment1, comment2);
    }

    @Test
    public void getAllCommentsTest() {
        Comment comment1 = new Comment(TEST_ID_1, TEST_ID_1, TEST_ID_1, TEST_COMMENT_TEXT);
        Comment comment2 = new Comment(TEST_ID_2, TEST_ID_1, TEST_ID_2, TEST_COMMENT_TEXT);
        List<Comment> commentList = Arrays.asList(comment1, comment2);
        when(commentRepository.findAll()).thenReturn(commentList);

        List<Comment> resultList = commentService.getAllComments();

        assertThat(resultList).contains(comment1, comment2);
    }
}
