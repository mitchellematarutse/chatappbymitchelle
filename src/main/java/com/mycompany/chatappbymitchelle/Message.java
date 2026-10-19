package com.mycompany.chatappbymitchelle;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public final class Message {

    // These are shared by all messages while the program runs
    private static ArrayList<Message> sentMessages = new ArrayList<>();
    private static ArrayList<Message> storedMessages = new ArrayList<>();
    private static int numMessages = 0; // counts how many messages were created

    private String messageID;
    private int messageNumber;
    private String recipient;
    private String messageText;
    private String messageHash;

    // Normal constructor: the ID and number are made automatically
    public Message(String recipient, String messageText) {
        this.messageID = String.format("%010d",
                ThreadLocalRandom.current().nextLong(10_000_000_000L));
        this.messageNumber = numMessages;
        numMessages++;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    // Constructor for the unit tests: we choose the ID and number ourselves
    public Message(String messageID, int messageNumber, String recipient, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    // The message ID must be no more than ten characters
    public boolean checkMessageID() {
        return messageID.length() <= 10;
    }

    // Same cell number check as Part 1 (starts with + and a country code)
    public static String checkRecipientCell(String cell) {
        if (cell.matches("^\\+\\d{1,3}\\d{1,10}$")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    // The message must not be more than 250 characters
    public static String checkMessageLength(String text) {
        if (text.length() <= 250) {
            return "Message ready to send.";
        }
        int extra = text.length() - 250;
        return "Message exceeds 250 characters by " + extra + "; please reduce the size.";
    }

    // Hash = first two digits of ID : message number : FIRSTWORDLASTWORD
    public String createMessageHash() {
        String[] words = messageText.trim().split("\\s+");
        String first = words[0].replaceAll("[^A-Za-z0-9]", "");
        String last = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");
        return (messageID.substring(0, 2) + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    public String messageIDGenerated() {
        return "Message ID generated: " + messageID;
    }

    // 1 = send, 2 = disregard, 3 = store
    public String sentMessage(int choice) {
        switch (choice) {
            case 1 -> {
                sentMessages.add(this);
                return "Message successfully sent.";
            }
            case 2 -> {
                return "Press 0 to delete the message.";
            }
            case 3 -> {
                storedMessages.add(this);
                storeMessage();
                return "Message successfully stored.";
            }
            default -> {
            }
        }
        return "Invalid choice.";
    }

    // Returns all the messages sent while the program is running
    public static String printMessages() {
        String result = "";
        for (Message m : sentMessages) {
            result = result + "Message ID: " + m.messageID + "\n"
                    + "Message Hash: " + m.messageHash + "\n"
                    + "Recipient: " + m.recipient + "\n"
                    + "Message: " + m.messageText + "\n\n";
        }
        return result;
    }

    /**
     *
     * @return
     */
    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    // Saves all the stored messages in a JSON file called messages.json
    public static void storeMessage() {
        String json = "[\n";
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            json = json + "  {\n"
                    + "    \"messageID\": \"" + clean(m.messageID) + "\",\n"
                    + "    \"messageHash\": \"" + clean(m.messageHash) + "\",\n"
                    + "    \"recipient\": \"" + clean(m.recipient) + "\",\n"
                    + "    \"message\": \"" + clean(m.messageText) + "\"\n"
                    + "  }";
            if (i < storedMessages.size() - 1) {
                json = json + ",";
            }
            json = json + "\n";
        }
        json = json + "]";

        try (FileWriter writer = new FileWriter("messages.json")) {
            writer.write(json);
        } catch (IOException e) {
            System.out.println("Could not save the messages file.");
        }
    }

    // Makes quotes and slashes safe to put inside JSON text
    private static String clean(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // Clears everything (used by the unit tests so each test starts fresh)
    public static void resetMessages() {
        sentMessages.clear();
        storedMessages.clear();
        numMessages = 0;
    }
}
