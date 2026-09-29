package com.mycompany.chatappbymitchelle;

public class Login {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String cellPhone;

    public Login(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Username must have an underscore and be no more than 5 characters
    public boolean checkUserName(String username) {
        return username.contains("_") && username.length() <= 5;
    }

    // Password needs 8+ characters, a capital, a number and a special character
    public boolean checkPasswordComplexity(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasCapital = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasCapital = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            } else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        return hasCapital && hasNumber && hasSpecial;
    }

    // Cell number must start with + and a country code, then no more than 10 digits
    // Regex idea based on the Oracle Java Tutorials on regular expressions (add your full reference in your reference list)
    public boolean checkCellPhoneNumber(String cellPhone) {
        return cellPhone.matches("^\\+\\d{1,3}\\d{1,10}$");
    }

    // ----- Messages for each check -----

    public String usernameMessage(String username) {
        if (checkUserName(username)) {
            return "Username successfully captured.";
        }
        return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
    }

    public String passwordMessage(String password) {
        if (checkPasswordComplexity(password)) {
            return "Password successfully captured.";
        }
        return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
    }

    public String cellPhoneMessage(String cellPhone) {
        if (checkCellPhoneNumber(cellPhone)) {
            return "Cell number successfully captured.";
        }
        return "Cell number is incorrectly formatted or does not contain an international code; please correct the number and try again.";
    }

    // Registers the user only if the username and password are both fine
    public String registerUser(String username, String password) {
        if (!checkUserName(username)) {
            return usernameMessage(username);
        }
        if (!checkPasswordComplexity(password)) {
            return passwordMessage(password);
        }

        this.username = username;
        this.password = password;
        return "Username successfully captured.\nPassword successfully captured.\nUser has been registered successfully.";
    }

    // Checks the login details against what was stored at registration
    public boolean loginUser(String enteredUsername, String enteredPassword) {
        return enteredUsername.equals(this.username)
                && enteredPassword.equals(this.password);
    }

    // Gives the message for a successful or failed login
    public String returnLoginStatus(boolean loggedIn) {
        if (loggedIn) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }
}
