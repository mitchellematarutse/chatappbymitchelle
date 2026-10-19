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

        // Only go on to the messages if the login worked
        if (!loggedIn) {
            return;
        }

        // ----- MESSAGES -----
        System.out.println("\n=== SEND MESSAGES ===");
        System.out.print("How many messages do you want to enter? ");
        int numMessages = Integer.parseInt(input.nextLine());

        for (int i = 1; i <= numMessages; i++) {
            System.out.println("\n--- Message " + i + " ---");

            // Recipient
            String recipient;
            String cellResult;
            do {
                System.out.print("Enter the recipient cell number (e.g. +27718693002): ");
                recipient = input.nextLine();
                cellResult = Message.checkRecipientCell(recipient);
                System.out.println(cellResult);
            } while (!cellResult.equals("Cell phone number successfully captured."));

            // Message text
            String text;
            String lengthResult;
            do {
                System.out.print("Enter your message (max 250 characters): ");
                text = input.nextLine();
                lengthResult = Message.checkMessageLength(text);
                System.out.println(lengthResult);
            } while (!lengthResult.equals("Message ready to send."));

            Message message = new Message(recipient, text);
            System.out.println(message.messageIDGenerated());

            // Send, disregard or store
            int choice = 0;
            while (choice < 1 || choice > 3) {
                System.out.println("Choose an option:");
                System.out.println("1) Send Message");
                System.out.println("2) Disregard Message");
                System.out.println("3) Store Message to send later");
                choice = Integer.parseInt(input.nextLine());
            }

            System.out.println(message.sentMessage(choice));

            if (choice == 2) {
                // Wait for the user to press 0 to delete the message
                String press = "";
                while (!press.equals("0")) {
                    press = input.nextLine();
                }
                System.out.println("Message deleted.");
            }

            // Show the full details after it is sent
            if (choice == 1) {
                System.out.println("\nMessage ID: " + message.messageIDGenerated().replace("Message ID generated: ", ""));
                System.out.println("Message Hash: " + message.createMessageHash());
                System.out.println("Recipient: " + recipient);
                System.out.println("Message: " + text);
            }
        }

        System.out.println("\n=== ALL SENT MESSAGES ===");
        System.out.println(Message.printMessages());
        System.out.println("Total messages sent: " + Message.returnTotalMessages());
    }
}