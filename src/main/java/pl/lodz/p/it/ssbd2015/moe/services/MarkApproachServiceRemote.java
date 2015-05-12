package pl.lodz.p.it.ssbd2015.moe.services;

import pl.lodz.p.it.ssbd2015.entities.AnswerEntity;
import pl.lodz.p.it.ssbd2015.entities.ApproachEntity;
import pl.lodz.p.it.ssbd2015.exceptions.ApplicationBaseException;

import javax.ejb.Remote;
import java.util.List;

/**
 * Intefejs służy do oceniania podejść.
 * @author Bartosz Ignaczewski
 */
@Remote
public interface MarkApproachServiceRemote {

	/**
	 * Wyszukuje z bazy danych podejście o okreslonym id
	 * @param id identyfikator podejścia które chcemy pobrać z bazy
	 * @return obiekt ApproachEntity
	 * @throws ApplicationBaseException
	 */
	ApproachEntity findById(long id) throws ApplicationBaseException;

	/**
	 * Metoda pozwala ocenić podejście
	 * @param gradedAnswers lista z wypełnionymi ocenami w odpowiedziach
	 * @throws ApplicationBaseException
	 */
	void mark(List<AnswerEntity> gradedAnswers) throws ApplicationBaseException;

	void disqualify() throws ApplicationBaseException;

}
