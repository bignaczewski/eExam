package pl.lodz.p.it.ssbd2015.mre.managers;

import pl.lodz.p.it.ssbd2015.entities.AnswerEntity;
import pl.lodz.p.it.ssbd2015.entities.ApproachEntity;
import pl.lodz.p.it.ssbd2015.entities.ExamEntity;
import pl.lodz.p.it.ssbd2015.exceptions.ApplicationBaseException;

import javax.ejb.Local;
import java.util.List;

/**
 * Interfejs manager do zarządzaniem podejściami od strony ucznia i opiekuna
 * @author Bartosz Ignaczewski
 */
@Local
public interface AnswersManagerLocal {

    /**
     * Tworzy nowe podejście do egzaminu
     * @param title tytuł egzaminu, którego podejście chcemy utworzyć
     * @return identyfikator nowo utworzonego podejścia
     * @throws ApplicationBaseException Rzucany, kiedy nie utworzy nowego podejścia do egzaminu
     */
    Long createApproach(String title) throws ApplicationBaseException;

    /**
     * Metoda edytuje podejści. Dodaje odpowiedzi ucznia.
     * @param approach Podejście które poddajemy edycji
     * @param answers opdowiedzi, których udzielił student
     * @throws ApplicationBaseException Rzucany, kiedy nie zedytuje podejścia
     */
    void editApproach(ApproachEntity approach, List<AnswerEntity> answers) throws ApplicationBaseException;

    /**
     * Kończy rozpoczęte podejście.
     * @param approach Podejście które wymaga zakończenia
     * @throws ApplicationBaseException Rzucany, kiedy nie zakończy rozpoczętego podejścia
     */
    void endApproach(ApproachEntity approach) throws ApplicationBaseException;


    /**
     * Pobiera wszystkie egzaminy do których użytkownik może podejść.
     * @return Egzaminy, do których użytkownik może podejść
     */
    List<ExamEntity> findAvailableExams();

}
