package com.mycompany.chatappbymitchelle;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    // ----- assertEquals tests (test data from the brief) -----

    @Test
    public void testUsernameCorrectlyFormatted() {
        Login login = new Login("Kyle", "Smith");
        login.registerUser("kyl_1", "Ch&&sec@ke99!");
        boolean loggedIn = login.loginUser("kyl_1", "Ch&&sec@ke99!");
        assertEquals("Welcome Kyle, Smith it is great to see you again.",
                login.returnLoginStatus(loggedIn));
    }

    @Test
    public void testUsernameIncorrectlyFormatted() {
        Login login = new Login("Kyle", "Smith");
        assertEquals("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.",
                login.usernameMessage("kyle!!!!!!!"));
    }

    @Test
    public void testPasswordMeetsComplexity() {
        Login login = new Login("Kyle", "Smith");
        assertEquals("Password successfully captured.",
                login.passwordMessage("Ch&&sec@ke99!"));
    }

    @Test
    public void testPasswordDoesNotMeetComplexity() {
        Login login = new Login("Kyle", "Smith");
        assertEquals("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.",
                login.passwordMessage("password"));
    }

    @Test
    public void testCellPhoneCorrect() {
        Login login = new Login("Kyle", "Smith");
        assertEquals("Cell number successfully captured.",
                login.cellPhoneMessage("+27838968976"));
    }

    @Test
    public void testCellPhoneIncorrect() {
        Login login = new Login("Kyle", "Smith");
        assertEquals("Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.",
                login.cellPhoneMessage("08966553"));
    }

    // ----- assertTrue / assertFalse tests -----

    @Test
    public void testLoginSuccessful() {
        Login login = new Login("Kyle", "Smith");
        login.registerUser("kyl_1", "Ch&&sec@ke99!");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginFailed() {
        Login login = new Login("Kyle", "Smith");
        login.registerUser("kyl_1", "Ch&&sec@ke99!");
        assertFalse(login.loginUser("kyl_1", "wrongPass1!"));
    }

    @Test
    public void testUsernameCorrectlyFormattedTrue() {
        Login login = new Login("Kyle", "Smith");
        assertTrue(login.checkUserName("kyl_1"));
    }

    @Test
    public void testUsernameIncorrectlyFormattedFalse() {
        Login login = new Login("Kyle", "Smith");
        assertFalse(login.checkUserName("kyle!!!!!!!"));
    }

    @Test
    public void testPasswordComplexityTrue() {
        Login login = new Login("Kyle", "Smith");
        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    public void testPasswordComplexityFalse() {
        Login login = new Login("Kyle", "Smith");
        assertFalse(login.checkPasswordComplexity("password"));
    }

    @Test
    public void testCellPhoneTrue() {
        Login login = new Login("Kyle", "Smith");
        assertTrue(login.checkCellPhoneNumber("+27838968976"));
    }

    @Test
    public void testCellPhoneFalse() {
        Login login = new Login("Kyle", "Smith");
        assertFalse(login.checkCellPhoneNumber("08966553"));
    }
}