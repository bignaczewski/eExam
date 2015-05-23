package pl.lodz.p.it.ssbd2015.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa pomocnicza do widoków jsf, umożliwiajaca stworzenie tabelek, w których można zaznaczyć wiele rzędów.
 * @author Michał Sośnicki <sosnicki.michal@gmail.com>
 */
public class SelectableItem<T> implements Serializable {

    private T item;

    private boolean selected;

    public SelectableItem(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public static <T> List<SelectableItem<T>> wrap(Iterable<T> iterable) {
        List<SelectableItem<T>> wrapped = new ArrayList<>();
        iterable.forEach(item -> wrapped.add(new SelectableItem<>(item)));
        return wrapped;
    }
}