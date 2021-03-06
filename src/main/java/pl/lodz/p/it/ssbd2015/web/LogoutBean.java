package pl.lodz.p.it.ssbd2015.web;

import javax.enterprise.context.Dependent;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bean udostępnia możliwość wylogowywania.
 * @author Piotr Jurewicz
 */
@Named(value = "logoutBean")
@Dependent
public class LogoutBean {

    private static final Logger logger = Logger.getLogger(LogoutBean.class.getName());

    /**
     * Metoda niszczy sesję użytkonika, co powoduje wylogowanie go z aplikacji.
     * @return Strona na którą uzytkownik ma trafić po wylogowaniu.
     */
    public String logout() {
        String result = "/index";

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            logger.log(Level.FINE, "Wylogowywanie użytkownika...");
            request.logout();
        } catch (ServletException e) {
            logger.log(Level.SEVERE, "Błąd podczas wylogowywania użytkownika!", e);
        }

        return result;
    }
}
