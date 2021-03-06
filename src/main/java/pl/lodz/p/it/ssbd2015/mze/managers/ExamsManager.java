package pl.lodz.p.it.ssbd2015.mze.managers;

import pl.lodz.p.it.ssbd2015.entities.ExamEntity;
import pl.lodz.p.it.ssbd2015.entities.ExaminerEntity;
import pl.lodz.p.it.ssbd2015.entities.QuestionEntity;
import pl.lodz.p.it.ssbd2015.entities.TeacherEntity;
import pl.lodz.p.it.ssbd2015.entities.services.LoggingInterceptor;
import pl.lodz.p.it.ssbd2015.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2015.exceptions.mze.*;
import pl.lodz.p.it.ssbd2015.mze.facades.ExamEntityFacadeLocal;
import pl.lodz.p.it.ssbd2015.mze.facades.ExaminerEntityFacadeLocal;
import pl.lodz.p.it.ssbd2015.mze.facades.TeacherEntityFacadeLocal;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.*;
import javax.interceptor.Interceptors;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static pl.lodz.p.it.ssbd2015.utils.ExceptionUtils.elvis;

/**
 * Implementacja interfejsu {@link ExamsManagerLocal}, pozwala na zarządzanie Egzaminami
 * @author Andrzej Kurczewski
 * @author Bartosz Ignaczewski
 */
@Stateless(name = "pl.lodz.p.it.ssbd2015.mze.managers.ExamsManager")
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Interceptors(LoggingInterceptor.class)
public class ExamsManager implements ExamsManagerLocal {

    @EJB
    private ExamEntityFacadeLocal examEntityFacade;

    @EJB
    private ExaminerEntityFacadeLocal examinerEntityFacade;

    @EJB
    private TeacherEntityFacadeLocal teacherEntityFacade;

    @Resource
    private SessionContext sessionContext;

    @Override
    @RolesAllowed("CREATE_EXAM_MZE")
    public void createExam(ExamEntity exam, List<QuestionEntity> questions, List<TeacherEntity> teachers) throws ApplicationBaseException {
    	String name = elvis(() -> sessionContext.getCallerPrincipal().getName());
    	ExaminerEntity examiner = examinerEntityFacade.findByLogin(name)
    			.orElseThrow(() -> new ExaminerNotFoundException("Could not find ExaminerEntity for logged user " + name));
    	exam.setCreator(examiner);

    	questions.forEach(question -> {
    		exam.getQuestions().add(question);
    		question.getExams().add(exam);
    	});
    	teachers.forEach(teacher -> {
    		exam.getTeachers().add(teacher);
    		teacher.getExams().add(exam);
    	});

    	examEntityFacade.create(exam);
    }

    @Override
    @RolesAllowed("CLONE_EXAM_MZE")
    public void cloneExam(ExamEntity exam) throws ApplicationBaseException {
        String login = sessionContext.getCallerPrincipal().getName();
        ExaminerEntity examiner = examinerEntityFacade.findByLogin(login).orElseThrow(() -> new ExaminerNotFoundException("Examiner with login " + login + "does not exist"));

        ExamEntity newExam = new ExamEntity();
        newExam.setCreator(examiner);
        newExam.setCountTakeExam(exam.getCountTakeExam());
        newExam.setCountQuestion(exam.getCountQuestion());
        newExam.setDuration(exam.getDuration());
        newExam.setDateStart(exam.getDateStart());
        newExam.setDateEnd(exam.getDateEnd());
        newExam.setQuestions(exam.getQuestions());
        newExam.setTeachers(exam.getTeachers());

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date date = new Date(timestamp.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH mm ss SS");
        String now = simpleDateFormat.format(date);

        String title = exam.getTitle();
        int length = (title + " " + now).length();
        if(length >= 100) {
            title = title.substring(0, title.length() - (length - 100));
        }

        newExam.setTitle(title + " " + now);

        examEntityFacade.create(newExam);
    }

    @Override
    @RolesAllowed("ADD_TEACHER_TO_EXAM_MZE")
    public List<TeacherEntity> findAllNotInExam(ExamEntity exam) throws ApplicationBaseException {
    	return teacherEntityFacade.findAllNotInExam(exam);
    }

    @Override
    @RolesAllowed("EDIT_EXAM_MZE")
    public void editExam(ExamEntity exam, ExamEntity newExam) throws ApplicationBaseException {
        if (newExam.getDateStart() != null && newExam.getDateEnd() != null && !newExam.getDateEnd().after(newExam.getDateStart())) {
            throw new ExamEndBeforeStartException("End date to " + newExam.getDateEnd() + " which is before start date " + newExam.getDateStart());
        }

        exam.setCountQuestion(newExam.getCountQuestion());
        exam.setCountTakeExam(newExam.getCountTakeExam());
        exam.setDateStart(newExam.getDateStart());
        exam.setDateEnd(newExam.getDateEnd());
        exam.setDuration(newExam.getDuration());
        exam.setTitle(newExam.getTitle());

        String login = sessionContext.getCallerPrincipal().getName();
        ExaminerEntity modifier = examinerEntityFacade.findByLogin(login).orElseThrow(() -> new ExaminerNotFoundException("Examiner with login " + login + "does not exist"));
        exam.setModifier(modifier);

        examEntityFacade.edit(exam);
    }

    @Override
    @RolesAllowed("ADD_TEACHER_TO_EXAM_MZE")
    public void addTeacher(ExamEntity exam, TeacherEntity teacher) throws ApplicationBaseException {
        String login = sessionContext.getCallerPrincipal().getName();
        ExaminerEntity examiner = examinerEntityFacade.findByLogin(login)
                .orElseThrow(() -> new ExaminerNotFoundException("Examiner with login " + login + "does not exist"));

        exam.getTeachers().add(teacher);
        exam.setModifier(examiner);

        examEntityFacade.edit(exam);
    }

    @Override
    @RolesAllowed("REMOVE_QUESTION_FROM_EXAM_MZE")
    public void removeQuestion(ExamEntity exam, long questionId) throws ApplicationBaseException {
        if (!exam.getApproaches().isEmpty()) {
            throw new ExamApproachesExistException("Could not remove Question with id = " + questionId
                    + " from exam " + exam + " because there are approaches to the exam.");
        }

        boolean found = false;

        for (Iterator<QuestionEntity> it = exam.getQuestions().iterator(); it.hasNext();) {
            QuestionEntity question = it.next();
            if (question.getId() == questionId) {
                it.remove();
                found = true;
                break;
            }
        }

        if (!found) {
            throw new ExamIllegalArgumentException("Attempt to remove Question with id = " + questionId
                    + " from exam " + exam + " failed because it didn't have this question.");
        }

        String login = sessionContext.getCallerPrincipal().getName();
        ExaminerEntity modifier = examinerEntityFacade.findByLogin(login)
                .orElseThrow(() -> new ExaminerNotFoundException("Examiner with login " + login + "does not exist"));
        exam.setModifier(modifier);

        examEntityFacade.edit(exam);
    }

    @Override
    @RolesAllowed("REMOVE_TEACHER_FROM_EXAM_MZE")
    public void removeTeacher(ExamEntity exam, long teacherId) throws ApplicationBaseException {
        boolean found = false;

        for (Iterator<TeacherEntity> it = exam.getTeachers().iterator(); it.hasNext();) {
            TeacherEntity teacherEntity = it.next();
            if (teacherEntity.getId() == teacherId) {
                it.remove();
                found = true;
                break;
            }
        }

        if (!found) {
            throw new ExamTeacherNotFoundException("Attempt to remove  teacher with id = " +  teacherId
                    + " from exam " + exam + " failed because it didn't have this teacher.");
        }

        String login = sessionContext.getCallerPrincipal().getName();
        ExaminerEntity modifier = examinerEntityFacade.findByLogin(login)
                .orElseThrow(() -> new ExaminerNotFoundException("Examiner with login " + login + "does not exist"));
        exam.setModifier(modifier);

        examEntityFacade.edit(exam);
    }
}
