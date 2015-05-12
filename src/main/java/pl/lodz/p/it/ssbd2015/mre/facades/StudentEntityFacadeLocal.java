package pl.lodz.p.it.ssbd2015.mre.facades;

import pl.lodz.p.it.ssbd2015.entities.StudentEntity;
import pl.lodz.p.it.ssbd2015.entities.facade.Read;

import javax.ejb.Local;
import java.util.Optional;

/**
 * Interfejs do operacji bazodanowych dla encji {@link StudentEntity}
 * @author Michał Sośnicki <sosnicki.michal@gmail.com>
 * @author Andrzej Kurczewski
 */
@Local
public interface StudentEntityFacadeLocal extends Read<Long, StudentEntity> {
    Optional<StudentEntity> findByLogin(String login);
}
