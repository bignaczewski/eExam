package pl.lodz.p.it.ssbd2015.mze.services;

import pl.lodz.p.it.ssbd2015.entities.ExamEntity;
import pl.lodz.p.it.ssbd2015.entities.QuestionEntity;
import pl.lodz.p.it.ssbd2015.entities.TeacherEntity;
import pl.lodz.p.it.ssbd2015.exceptions.ApplicationBaseException;

import javax.ejb.Remote;
import java.util.List;

/**
 * Interfejs wykorzystywany do tworzenia pytań, wyszkiwania egzaminów, pytań i nauczycieli.
 * @author Bartosz Ignaczewski
 */
@Remote
public interface ExamsServiceRemote {

    /**
     * Dla przypadku MZE.11:
     * Egzaminator wyświetla szczegóły egzaminu, to znaczy jego właściwości,
     * a także listę przypisanych do niego pytań i nauczycieli.
     * Dla przypadku MZE.13:
     * Egzaminator wyświetla raport dla danego egzaminu.
     * @param id id danego egzaminu
     * @return Encja danego egzaminu, jeżeli istnieje
     * @throws ApplicationBaseException wyjątek rzucany w przypadku braku encji w bazie
     */
    ExamEntity findById(long id) throws ApplicationBaseException;

    /**
     * Egzaminator tworzy pytanie, które może zostać później wykorzystane podczas tworzenia egzaminu.
     * @param questionEntity encja z danymi które mają zostać utrwalone w bazie danych
     * @throws ApplicationBaseException jeżeli zapisywanie danych do bazy naruszy jakiś constraint lub dane nie będą poprawne
     */
    void create(QuestionEntity questionEntity) throws ApplicationBaseException;

    /**
     * Wyświetla listę wszystkich pytań z systemu.
     * @return Lista istniejących pytań.
     */
    List<QuestionEntity> findAllQuestions();

    /**
     * Wyświetla wszystkich nauczycieli w systemie. Za nauczycieli uznaje przy tym tylko aktywne,
     * potwierdzone osoby z istniejącą i aktywną rolą nauczyciela.
     * @return Lista nauczycieli w systemie.
     */
    List<TeacherEntity> findAllTeachers();

}
