package pl.lodz.p.it.ssbd2015.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michał Sośnicki
 */
@Entity
@DiscriminatorValue(Groups.GUARDIAN)
@NamedQuery(name = "findGuardianByLogin", query = "SELECT e FROM GuardianEntity e WHERE e.login = :login")
public class GuardianEntity extends GroupsEntity {

    @OneToMany(mappedBy = "guardian")
    private List<StudentEntity> guarded = new ArrayList<>();

    public List<StudentEntity> getGuarded() {
        return guarded;
    }

    public void setGuarded(List<StudentEntity> guarded) {
        this.guarded = guarded;
    }

    @Override
    public String toString() {
        return "GuardianEntity{" + super.toString() + "}";
    }
}
