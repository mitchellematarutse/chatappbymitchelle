package com.mycompany.chatappbymitchelle;

import java.util.Scanner;

/**
 *
 * @author Mitchelle Lerato
 */
public class Chatappbymitchelle {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("=== REGISTRATION ===");
        System.out.print("Enter your first name: ");
        String firstName = input.nextLine();
        System.out.print("Enter your last name: ");
        String lastName = input.nextLine();

        Login login = new Login(firstName, lastName);

        // Username
        String username;
        do {
            System.out.print("Enter a username: ");
            username = input.nextLine();
            System.out.println(login.usernameMessage(username));
        } while (!login.checkUserName(username));

        // Password
        String password;
        do {
            System.out.print("Enter a password: ");
            password = input.nextLine();
            System.out.println(login.passwordMessage(password));
        } while (!login.checkPasswordComplexity(password));

        // Cell phone
        String cellPhone;
        do {
            System.out.print("Enter your cell number (e.g. +27838968976): ");
            cellPhone = input.nextLine();
            System.out.println(login.cellPhoneMessage(cellPhone));
        } while (!login.checkCellPhoneNumber(cellPhone));

        System.out.println(login.registerUser(username, password));

        // Login
        System.out.println("\n=== LOGIN ===");
        System.out.print("Enter your username: ");
        String loginUsername = input.nextLine();
        System.out.print("Enter your password: ");
        String loginPassword = input.nextLine();

        boolean loggedIn = login.loginUser(loginUsername, loginPassword);
        System.out.println(login.returnLoginStatus(loggedIn));
    }
}