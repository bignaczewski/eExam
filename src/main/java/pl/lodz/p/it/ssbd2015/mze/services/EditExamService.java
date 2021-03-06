package pl.lodz.p.it.ssbd2015.mze.services;

import pl.lodz.p.it.ssbd2015.entities.ExamEntity;
import pl.lodz.p.it.ssbd2015.entities.TeacherEntity;
import pl.lodz.p.it.ssbd2015.entities.services.BaseStatefulService;
import pl.lodz.p.it.ssbd2015.entities.services.LoggingInterceptor;
import pl.lodz.p.it.ssbd2015.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2015.exceptions.mze.TeacherNotFoundException;
import pl.lodz.p.it.ssbd2015.exceptions.mze.ExamNotFoundException;
import pl.lodz.p.it.ssbd2015.mze.facades.ExamEntityFacadeLocal;
import pl.lodz.p.it.ssbd2015.mze.facades.TeacherEntityFacadeLocal;
import pl.lodz.p.it.ssbd2015.mze.managers.ExamsManagerLocal;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementacje Endpointu zgodnie z interfejsem {@link EditExamServiceRemote}.
 * Klasa przechowuje pole exam oraz teachesNotInExam.
 * @author Bartosz Ignaczewski on 04.05.15.
 * @author Andrzej Kurczewski
 */
@Stateful(name = "pl.lodz.p.it.ssbd2015.mze.services.EditExamService")
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors(LoggingInterceptor.class)
public class EditExamService extends BaseStatefulService implements EditExamServiceRemote {

    @EJB
    private ExamEntityFacadeLocal examEntityFacade;

    @EJB
    private TeacherEntityFacadeLocal teacherEntityFacade;

    @EJB
    private ExamsManagerLocal examsManager;

    private ExamEntity exam;

    private List<TeacherEntity> teachersNotInExam;

    @Override
    @RolesAllowed("EDIT_EXAM_MZE")
    public ExamEntity findById(long examId) throws ApplicationBaseException {
        exam = examEntityFacade.findById(examId).orElseThrow(() -> new ExamNotFoundException("Exam with id = " + examId + " does not exist"));

        exam.getQuestions().isEmpty();
        exam.getTeachers().isEmpty();
        exam.getApproaches().isEmpty();
        return exam;
    }

    @Override
    @RolesAllowed("ADD_TEACHER_TO_EXAM_MZE")
    public List<TeacherEntity> findAllNotInExam() throws ApplicationBaseException {

    	teachersNotInExam= examsManager.findAllNotInExam(this.exam).stream()
        .filter(t -> t.isActive() && t.isConfirm() && t.isGroupActive()).collect(Collectors.toList());

        return teachersNotInExam;
    }

    @Override
    @RolesAllowed("EDIT_EXAM_MZE")
    public void editExam(ExamEntity exam) throws ApplicationBaseException {
    	examsManager.editExam(this.exam, exam);
    }

    @Override
    @RolesAllowed("ADD_TEACHER_TO_EXAM_MZE")
    public void addTeacher(long teacherId) throws ApplicationBaseException {
        TeacherEntity teacher = teachersNotInExam.stream()
                .filter(t -> t.getId() == teacherId)
                .findFirst()
                .orElseThrow(() -> new TeacherNotFoundException("Teacher with id " + teacherId + " was not found."));

        examsManager.addTeacher(this.exam, teacher);
    }

    @Override
    @RolesAllowed("REMOVE_QUESTION_FROM_EXAM_MZE")
    public void removeQuestion(long questionId) throws ApplicationBaseException {
        examsManager.removeQuestion(exam, questionId);
    }

    @Override
    @RolesAllowed("REMOVE_TEACHER_FROM_EXAM_MZE")
    public void removeTeacher(long teacherId) throws ApplicationBaseException {
    	examsManager.removeTeacher(exam, teacherId);
    }
}
