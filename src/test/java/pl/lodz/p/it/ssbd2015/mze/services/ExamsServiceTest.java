package pl.lodz.p.it.ssbd2015.mze.services;

import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.junit.Test;
import pl.lodz.p.it.ssbd2015.BaseArquillianTest;
import pl.lodz.p.it.ssbd2015.entities.ExamEntity;
import pl.lodz.p.it.ssbd2015.entities.QuestionEntity;
import pl.lodz.p.it.ssbd2015.exceptions.mze.ExamNotFoundException;

import javax.ejb.EJB;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Andrzej Kurczewski
 * @author Adam Król
 * @author Piotr Jurewicz
 */
@UsingDataSet({"ValidUser.yml", "mze/ExamsServiceTest.yml"})
public class ExamsServiceTest extends BaseArquillianTest {

    @EJB
    private ExamsServiceRemote examsService;

    @Test
    @ShouldMatchDataSet(value = "mze/expected-ExamsServiceTest#shouldCreateQuestion.yml",
            excludeColumns = {"question.question_id", "question.question_date_add", "question.question_date_modification"})
    public void shouldCreateQuestion() throws Exception {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent("Bla bla bla");
        questionEntity.setSampleAnswer("Pan da 3");
        examsService.create(questionEntity);
    }

    @Test
    public void shouldFindExam() throws Exception {
        ExamEntity examEntity = examsService.findById(1);
        assertThat(examEntity, notNullValue());
    }

    @Test(expected = ExamNotFoundException.class)
    public void shouldNotFindExam() throws Exception {
        examsService.findById(2);
    }

    @Test
    public void shouldReturnExistingExam() throws Exception {
        long id = 1l;
        ExamEntity exam = examsService.findById(id);
        assertThat(String.format("Exam with id = %d can be found.", id), exam.getId(), is(id));
    }

    @Test(expected = ExamNotFoundException.class)
    public void shouldThrowExceptionWhenExamDoNotExist() throws Exception {
        examsService.findById(20l);
    }
}
