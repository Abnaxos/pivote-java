package ch.piratenpartei.pivote.model;

import java.beans.PropertyChangeListener;

import ch.raffael.util.beans.Observable;
import ch.raffael.util.beans.ObservableSupport;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ModelBean implements Observable {

    protected final ObservableSupport observableSupport = new ObservableSupport(this);

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.removePropertyChangeListener(listener);
    }

}
