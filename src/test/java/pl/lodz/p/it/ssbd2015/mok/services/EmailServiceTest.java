package pl.lodz.p.it.ssbd2015.mok.services;

import org.jboss.arquillian.persistence.UsingDataSet;
import org.junit.Test;
import pl.lodz.p.it.ssbd2015.BaseArquillianTest;
import pl.lodz.p.it.ssbd2015.exceptions.ApplicationBaseException;
import pl.lodz.p.it.ssbd2015.mok.managers.EmailManagerLocal;

import javax.ejb.EJB;

/**
 * @author Bartosz Ignaczewski
 */
@UsingDataSet({"ValidUser.yml"})
public class EmailServiceTest extends BaseArquillianTest {

    @EJB
    private EmailManagerLocal emailService;

    @Test
    public void sendEmail() throws ApplicationBaseException {
    	emailService.sendEmail("foo@foo.bar", "test", "test - body");
    }

}
