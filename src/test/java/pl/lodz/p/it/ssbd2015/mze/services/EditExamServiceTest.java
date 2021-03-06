package pl.lodz.p.it.ssbd2015.mze.services;

import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Test;
import pl.lodz.p.it.ssbd2015.BaseArquillianTest;
import pl.lodz.p.it.ssbd2015.entities.ExamEntity;
import pl.lodz.p.it.ssbd2015.entities.QuestionEntity;
import pl.lodz.p.it.ssbd2015.entities.TeacherEntity;
import pl.lodz.p.it.ssbd2015.exceptions.mze.ExamApproachesExistException;
import pl.lodz.p.it.ssbd2015.exceptions.mze.ExamOptimisticLockException;
import pl.lodz.p.it.ssbd2015.mre.services.AnswerServiceRemote;

import javax.ejb.EJB;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * @author Michał Sośnicki
 * Created by Tobiasz Kowalski on 26.05.15.
 */
@UsingDataSet({"ValidUser.yml", "mze/EditExamServiceTest.yml"})
public class EditExamServiceTest extends BaseArquillianTest {

    @EJB
    private EditExamServiceRemote editExamService;

    @EJB
    private EditExamServiceRemote editExamService2;

    @EJB
    private AnswerServiceRemote answerService;

    @Test
    @Transactional(TransactionMode.DISABLED)
    @UsingDataSet({"ValidUser.yml", "mze/EditExamServiceTest#shouldRemoveAQuestion.yml"})
    @ShouldMatchDataSet(value = "mze/expected-EditExamServiceTest#shouldRemoveAQuestion.yml")
    public void shouldRemoveAQuestion() throws Exception {
        ExamEntity exam = editExamService.findById(3l);  // Przy okazji sprawdzę, czy exam się poprawnie podmieni w środku
        exam = editExamService.findById(1l);

        for (QuestionEntity question : exam.getQuestions()) {
            if (question.getId() == 3) {
                editExamService.removeQuestion(3);
                break;
            }
        }
    }

    @Test
    @Transactional(TransactionMode.DISABLED)
    @UsingDataSet({"ValidUser.yml", "mze/EditExamServiceTest#shouldRemoveATeacher.yml"})
    @ShouldMatchDataSet(value = "mze/expected-EditExamServiceTest#shouldRemoveATeacher.yml")
    public void shouldRemoveATeacher() throws Exception {
        ExamEntity exam = editExamService.findById(1l);

        for (TeacherEntity teacherEntity  : exam.getTeachers()) {
            if (teacherEntity.getId() == 6) {
                editExamService.removeTeacher(teacherEntity.getId());
                break;
            }
        }
    }

    @Test
    @Transactional(TransactionMode.DISABLED)
    @UsingDataSet({"ValidUser.yml", "mze/EditExamServiceTest#shouldRemoveAQuestion.yml"})
    @ShouldMatchDataSet(value = "mze/expected-EditExamServiceTest#shouldRemoveTheOnlyQuestion.yml")
    public void shouldRemoveTheOnly() throws Exception {
        ExamEntity exam = editExamService.findById(2l);
        QuestionEntity theOnlyQuestion = exam.getQuestions().get(0);
        editExamService.removeQuestion(theOnlyQuestion.getId());
    }

    @Test(expected = ExamOptimisticLockException.class)
    @Transactional(TransactionMode.DISABLED)
    @UsingDataSet({"ValidUser.yml", "mze/EditExamServiceTest#shouldRemoveAQuestion.yml"})
    public void shouldRefuseConcurrentRemovalOnTheSameExam() throws Exception {
        ExamEntity exam1 = editExamService.findById(3l);

        ExamEntity exam2 = editExamService2.findById(3l);

        editExamService.removeQuestion(2l);

        editExamService2.removeQuestion(4l);
    }

    @Test(expected = ExamOptimisticLockException.class)
    @Transactional(TransactionMode.DISABLED)
    @UsingDataSet({"ValidUser.yml", "mze/EditExamServiceTest#shouldRemoveATeacher.yml"})
    public void shouldRefuseConcurrentRemovalTeacherOnTheSameExam() throws Exception {
        ExamEntity exam1 = editExamService.findById(1l);

        ExamEntity exam2 = editExamService2.findById(1l);

        editExamService.removeTeacher(7l);

        editExamService2.removeTeacher(6l);
    }

    @Test
    @Transactional(TransactionMode.DISABLED)
    @UsingDataSet({"ValidUser.yml", "mze/EditExamServiceTest#shouldRemoveAQuestion.yml"})
    @ShouldMatchDataSet(value = "mze/expected-EditExamServiceTest#shouldRemoveConcurrentlyFromDifferentExams.yml")
    public void shouldRemoveConcurrentlyFromDifferentExams() throws Exception {
        ExamEntity exam1 = editExamService.findById(1l);

        ExamEntity exam2 = editExamService2.findById(3l);

        editExamService.removeQuestion(4l);

        editExamService2.removeQuestion(4l);
    }

    @Test(expected = ExamApproachesExistException.class)
    @Transactional(TransactionMode.DISABLED)
    @UsingDataSet({"ValidUser.yml", "mze/EditExamServiceTest#shouldNotRemoveQuestionWhenApproachesExist.yml"})
    public void shouldNotRemoveQuestionWhenApproachesExist() throws Exception {
        ExamEntity exam = editExamService.findById(2l);
        QuestionEntity theOnlyQuestion = exam.getQuestions().get(0);
        editExamService.removeQuestion(theOnlyQuestion.getId());
    }

    @Test(expected = ExamOptimisticLockException.class)
    @Transactional(TransactionMode.DISABLED)
    @UsingDataSet({"ValidUser.yml", "mze/EditExamServiceTest#shouldNotRemoveQuestionWhenApproachStarts.yml"})
    public void shouldNotRemoveQuestionWhenApproachStarts() throws Exception {
        ExamEntity exam = editExamService.findById(1l);
        QuestionEntity theOnlyQuestion = exam.getQuestions().get(0);

        answerService.createApproach(exam.getTitle());

        editExamService.removeQuestion(theOnlyQuestion.getId());
    }

    @Test
    @Transactional(TransactionMode.DISABLED)
    public void shouldFindTeachersNotInExam() throws Exception {
        editExamService.findById(1);

        List<TeacherEntity> teachers = editExamService.findAllNotInExam();

        assertThat(teachers, hasSize(1));
    }

    @Test
    @Transactional(TransactionMode.DISABLED)
    public void shouldAddTeacherToExam() throws Exception{
        editExamService.findById(2);

        List<TeacherEntity> teachers = editExamService.findAllNotInExam();

        editExamService.addTeacher(7l);
    }


}
