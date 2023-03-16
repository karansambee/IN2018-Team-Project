package skywaysolutions.app.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Represents a SHA-256 hashed string with a salt.
 *
 * @author Alfred Manville
 */
public final class PasswordString {
    private final byte[] _salt;
    private final byte[] _hash;
    private MessageDigest _digest;

    /**
     * Constructs a new PasswordString with the specified hash and salt.
     *
     * @param hash The hash of the password.
     * @param salt The salt of the password.
     * @throws CheckedException Construction of the password string fails.
     */
    public PasswordString(byte[] hash, byte[] salt) throws CheckedException {
        _salt = salt;
        _hash = hash;
        createDigest();
        if (_hash == null || _hash.length != _digest.getDigestLength()) throw new CheckedException(new IllegalArgumentException("hash is not the same length as the digest"));
    }

    /**
     * Constructs a new PasswordString with the specified password to hash and a salt.
     *
     * @param password The password to hash.
     * @param salt The salt to use on the password for hashing.
     * @throws CheckedException Construction of the password string fails.
     */
    public PasswordString(String password, byte[] salt) throws CheckedException {
        if (password == null || password.equals("")) throw new CheckedException(new IllegalArgumentException("password is null or empty"));
        _salt = salt;
        createDigest();
        _hash = getHashFromPassword(password);
    }

    private void createDigest() throws CheckedException {
        if (_salt == null || _salt.length < 1) throw new CheckedException(new IllegalArgumentException("salt is null or empty"));
        try {
            _digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new CheckedException(e);
        }
    }

    private byte[] getHashFromPassword(String password) {
        _digest.reset();
        //The hash is calculated on the password as UTF-8 bytes with the salt append to it.
        byte[] bPassword = password.getBytes(StandardCharsets.UTF_8);
        byte[] dArr = new byte[bPassword.length+_salt.length];
        System.arraycopy(bPassword, 0, dArr, 0, bPassword.length);
        System.arraycopy(_salt, 0, dArr, bPassword.length, _salt.length);
        return _digest.digest(dArr);
    }

    /**
     * Gets the salt of the PasswordString.
     *
     * @return The salt.
     */
    public byte[] getSalt() {
        return _salt;
    }

    /**
     * Gets the hash of the PasswordString.
     *
     * @return The hash.
     */
    public byte[] getHash() {
        return _hash;
    }

    /**
     * Gets the digest of the PasswordString.
     *
     * @return The digest.
     */
    public MessageDigest getDigest() {
        return _digest;
    }

    /**
     * Checks the provided password is the same as this password.
     *
     * @param password The password to check.
     * @return If the passwords match.
     */
    public boolean checkPassword(String password) {
        byte[] bHash = getHashFromPassword(password);
        if (bHash.length != _hash.length) return false;
        for (int i = 0; i < bHash.length; i++) if (bHash[i] != _hash[i]) return false;
        return true;
    }

    /**
     * Gets a random 32 byte salt.
     *
     * @return The salt.
     * @throws CheckedException Salt calculation fails.
     */
    public static byte[] getRandomSalt() throws CheckedException {
        byte[] salt = new byte[32];
        try {
            SecureRandom.getInstanceStrong().nextBytes(salt);
        } catch (NoSuchAlgorithmException e) {
            throw new CheckedException(e);
        }
        return salt;
    }
}
