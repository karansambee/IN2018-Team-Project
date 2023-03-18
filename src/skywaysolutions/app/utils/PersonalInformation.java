package skywaysolutions.app.utils;

import java.util.Date;

/**
 *
 *
 * @author Samina Matin
 */
public final class PersonalInformation {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String emailAddress;
    private Date dateOfBirth;
    private String postcode;
    private String houseNumber;
    private String streetName;

    /**
     *
     *
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @param emailAddress
     * @param dateOfBirth
     * @param postcode
     * @param houseNumber
     * @param streetName
     */
    public PersonalInformation(String firstName, String lastName, String phoneNumber, String emailAddress, Date dateOfBirth, String postcode, String houseNumber, String streetName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.dateOfBirth = dateOfBirth;
        this.postcode = postcode;
        this.houseNumber = houseNumber;
        this.streetName = streetName;
    }

    /**
     * This gets the first name of the person.
     *
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * This sets the first name of the person.
     *
     * @param firstName The new first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }
}
