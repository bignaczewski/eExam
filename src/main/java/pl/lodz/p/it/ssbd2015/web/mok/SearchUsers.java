package pl.lodz.p.it.ssbd2015.web.mok;
import pl.lodz.p.it.ssbd2015.entities.PersonEntity;
import pl.lodz.p.it.ssbd2015.mok.services.PeopleServiceRemote;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import java.util.List;

/**
 * Backing bean do strony z listą wszystkich użytkowników.
 * @author Created by Marcin on 2015-04-22.
 */
@ManagedBean(name = "searchUsersMOK")
@RequestScoped
public class SearchUsers {

    @EJB
    private PeopleServiceRemote peopleService;

    private List<PersonEntity> personEntities;

    private String phrase;
    private boolean results;

    public void search() {
        if(phrase != null)
        {
            this.personEntities = peopleService.findPeopleByPhrase(phrase);
        }
        this.results = this.personEntities.size() == 0;
    }

    public List<PersonEntity> getPersonEntities() {
        return personEntities;
    }
    public void setPersonEntities(List<PersonEntity> personEntities)
    {
        this.personEntities = personEntities;
    }
    public void setPhrase(String phrase)
    {
        this.phrase = phrase;
    }
    public String getPhrase()
    {
        return this.phrase;
    }
    public boolean getResults()
    {
        return this.results;
    }
    public void setResults(boolean results)
    {
        this.results = results;
    }
}
