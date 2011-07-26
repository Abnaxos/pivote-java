package ch.piratenpartei.pivote.model;

import java.util.UUID;

import org.joda.time.LocalDate;

import ch.raffael.util.beans.Property;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Certificate extends ModelBean {

    private final Property<String> firstName = new Property<String>("firstName").bound(observableSupport);
    private final Property<String> lastName = new Property<String>("lastName").bound(observableSupport);
    private final Property<String> email = new Property<String>("email").bound(observableSupport);

    private final Property<Type> type = new Property<Type>("type").bound(observableSupport);
    private final Property<UUID> id = new Property<UUID>("id").bound(observableSupport);
    private final Property<String> description = new Property<String>("description").bound(observableSupport);

    private final Property<LocalDate> validFrom = new Property<LocalDate>("validFrom").bound(observableSupport);
    private final Property<LocalDate> validUntil = new Property<LocalDate>("validUntil").bound(observableSupport);

    public static enum Type {
        VOTER, AUTHORITY, NOTARY
    }

    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public Type getType() {
        return type.get();
    }

    public void setType(Type type) {
        this.type.set(type);
    }

    public UUID getId() {
        return id.get();
    }

    public void setId(UUID id) {
        this.id.set(id);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public LocalDate getValidFrom() {
        return validFrom.get();
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom.set(validFrom);
    }

    public LocalDate getValidUntil() {
        return validUntil.get();
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil.set(validUntil);
    }

}
