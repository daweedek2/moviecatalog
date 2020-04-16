package kostka.commentservice;

import kostka.commentservice.dto.CommentDto;
import kostka.commentservice.exception.CommentNotFoundException;
import kostka.commentservice.exception.InvalidDtoException;
import kostka.commentservice.model.Comment;
import kostka.commentservice.repository.CommentRepository;
import kostka.commentservice.service.CommentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CommentServiceApplication.class)
@Transactional
public class CommentServiceIntegrationTest {
    public static final String TEST_COMMENT_TEXT = "this is a test";
    public static final long TEST_ID_1 = 1L;
    public static final long TEST_ID_2 = 2L;
    public static final long TEST_ID_3 = 3L;
    public static final long TEST_NON_EXISTING_ID= -1L;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentService commentService;

    @Test
    public void createCommentValidDtoIntegrationTest() {
        CommentDto dto = new CommentDto(TEST_ID_1, TEST_ID_2, TEST_COMMENT_TEXT);
        int before = commentRepository.findAll().size();

        Comment comment = commentService.createComment(dto);

        assertThat(comment.getMovieId()).isEqualTo(TEST_ID_1);
        assertThat(comment.getAuthorId()).isEqualTo(TEST_ID_2);
        assertThat(comment.getCommentText()).isEqualTo(TEST_COMMENT_TEXT);
        assertThat(commentRepository.findAll().size()).isEqualTo(before + 1);
    }

    @Test
    public void createCommentInValidDtoIntegrationTest() {
        CommentDto dto = new CommentDto();
        dto.setAuthorId(TEST_ID_2);
        dto.setCommentText(TEST_COMMENT_TEXT);
        int before = commentRepository.findAll().size();

        assertThatThrownBy(() -> commentService.createComment(dto))
                .isInstanceOf(InvalidDtoException.class);
        assertThat(commentRepository.findAll().size()).isEqualTo(before);
    }

    @Test
    public void getExistingCommentIntegrationTest() {
        Comment comment = new Comment();
        comment.setMovieId(TEST_ID_1);
        comment.setAuthorId(TEST_ID_2);
        comment.setCommentText(TEST_COMMENT_TEXT);
        Comment dbComment = commentRepository.save(comment);

        Comment result = commentService.getComment(dbComment.getCommentId());

        assertThat(result).isEqualTo(dbComment);
    }

    @Test
    public void getNotExistingCommentIntegrationTest() {
        assertThatThrownBy(() -> commentService.getComment(TEST_NON_EXISTING_ID))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    public void getCommentsOfMovieIntegrationTest() {
        CommentDto dto1 = new CommentDto(TEST_ID_1, TEST_ID_2, TEST_COMMENT_TEXT);
        CommentDto dto2 = new CommentDto(TEST_ID_1, TEST_ID_2, TEST_COMMENT_TEXT);
        CommentDto dto3 = new CommentDto(TEST_ID_2, TEST_ID_2, TEST_COMMENT_TEXT);

        Comment comment1 = commentService.createComment(dto1);
        Comment comment2 = commentService.createComment(dto2);
        Comment comment3 = commentService.createComment(dto3);

        List<Comment> result = commentService.getAllCommentsForMovie(TEST_ID_1);

        assertThat(result).contains(comment1, comment2);
        assertThat(result).doesNotContain(comment3);
    }

    @Test
    public void getEmptyCommentsOfMovieIntegrationTest() {
        CommentDto dto1 = new CommentDto(TEST_ID_1, TEST_ID_2, TEST_COMMENT_TEXT);
        CommentDto dto2 = new CommentDto(TEST_ID_1, TEST_ID_2, TEST_COMMENT_TEXT);
        CommentDto dto3 = new CommentDto(TEST_ID_2, TEST_ID_2, TEST_COMMENT_TEXT);

        Comment comment1 = commentService.createComment(dto1);
        Comment comment2 = commentService.createComment(dto2);
        Comment comment3 = commentService.createComment(dto3);

        List<Comment> result = commentService.getAllCommentsForMovie(TEST_ID_3);

        assertThat(result).isEmpty();
    }

    @Test
    public void getAllCommentsIntegrationTest() {
        CommentDto dto1 = new CommentDto(TEST_ID_1, TEST_ID_2, TEST_COMMENT_TEXT);
        CommentDto dto2 = new CommentDto(TEST_ID_1, TEST_ID_2, TEST_COMMENT_TEXT);
        CommentDto dto3 = new CommentDto(TEST_ID_2, TEST_ID_2, TEST_COMMENT_TEXT);

        Comment comment1 = commentService.createComment(dto1);
        Comment comment2 = commentService.createComment(dto2);
        Comment comment3 = commentService.createComment(dto3);

        List<Comment> result = commentService.getAllComments();

        assertThat(result).contains(comment1, comment2, comment3);
        assertThat(result.size()).isEqualTo(3);
    }
}
