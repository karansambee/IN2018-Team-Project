package skywaysolutions.app.utils;

import java.util.Date;

/**
 * This is personalInformation class.
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
     * This constructs new personalInformation class.
     */
    public PersonalInformation() {
        this("", "", "", "", new Date(0), "", "", "");
    }

    /**
     * This constructs new personalInformation class.
     *
     * @param firstName This is the first name of the person.
     * @param lastName This is the last name of the person.
     * @param phoneNumber This is the phone number of the person.
     * @param emailAddress This is the email address of the person.
     * @param dateOfBirth This is the date of birth of the person.
     * @param postcode This is the postcode of the person.
     * @param houseNumber This is the house number of the person.
     * @param streetName This is the street name of the person.
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

    /**
     * This gets the last name of the person.
     *
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * This sets the last name of the person.
     *
     * @param lastName The new last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * This gets the phone number of the person.
     *
     * @return phoneNumber The phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * This sets the phone number of the person.
     *
     * @param phoneNumber The new phone number.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * This gets the email address of the person.
     *
     * @return emailAddress The email address.
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * This sets the email address of the person.
     *
     * @param emailAddress The new email address.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * This gets the date of birth of the person.
     *
     * @return dateOfBirth The date of birth.
     */
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * This sets the date of birth of the person.
     *
     * @param dateOfBirth The new date of birth.
     */
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * This gets the postcode of the person.
     *
     * @return postcode The postcode
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * This sets the postcode of the person.
     *
     * @param postcode The new postcode.
     */
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    /**
     * This gets the house number of the person.
     *
     * @return houseNumber The house number.
     */
    public String getHouseNumber() {
        return houseNumber;
    }

    /**
     * This sets the house number of the person.
     *
     * @param houseNumber The new house number.
     */
    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    /**
     * This gets the street number of the person.
     *
     * @return streetNumber The new street number.
     */
    public String getStreetName() {
        return streetName;
    }

    /**
     * This sets the street number of the person.
     *
     * @param streetName The new street number.
     */
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }
}
