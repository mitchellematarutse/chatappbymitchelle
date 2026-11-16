package com.mycompany.chatappbymitchelle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Message {

    // The arrays (lists) that hold everything while the program runs
    private static final ArrayList<Message> sentMessages = new ArrayList<>();
    private static final ArrayList<Message> disregardedMessages = new ArrayList<>();
    private static final ArrayList<Message> storedMessages = new ArrayList<>();
    private static final ArrayList<String> messageHashes = new ArrayList<>();
    private static final ArrayList<String> messageIDs = new ArrayList<>();

    private static int numMessages = 0; // counts how many messages were created
    private static String sender = "Developer";
    private static String storageFile = "messages.json";

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;

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

    // Constructor used when a message is read back from the JSON file
    private Message(String messageID, String messageHash, String recipient, String messageText) {
        this.messageID = messageID;
        this.messageHash = messageHash;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageNumber = numberFromHash(messageHash);
    }

    // Gets the message number out of a hash like 00:1:WHERETIME
    private static int numberFromHash(String hash) {
        try {
            return Integer.parseInt(hash.split(":")[1]);
        } catch (Exception e) {
            return 0;
        }
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageHash() {
        return messageHash;
    }

    // ----- Checks from Part 2 -----

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
        String cleanText = messageText.replaceAll("[^A-Za-z0-9\\s]", "").trim();
        String[] words = cleanText.split("\\s+");
        String first = words[0];
        String last = words[words.length - 1];
        return (messageID.substring(0, 2) + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    public String messageIDGenerated() {
        return "Message ID generated: " + messageID;
    }

    // ----- Filling the arrays -----

    // Puts a message into the right array using its flag (Sent, Stored or Disregard)
    public static void populate(Message m, String flag) {
        if (flag.equalsIgnoreCase("Sent")) {
            sentMessages.add(m);
        } else if (flag.equalsIgnoreCase("Stored")) {
            storedMessages.add(m);
        } else if (flag.equalsIgnoreCase("Disregard")) {
            disregardedMessages.add(m);
        } else {
            return;
        }
        messageHashes.add(m.messageHash);
        messageIDs.add(m.messageID);
    }

    // 1 = send, 2 = disregard, 3 = store
    public String sentMessage(int choice) {
        if (choice == 1) {
            populate(this, "Sent");
            return "Message successfully sent.";
        } else if (choice == 2) {
            populate(this, "Disregard");
            return "Press 0 to delete the message.";
        } else if (choice == 3) {
            populate(this, "Stored");
            storeMessage();
            return "Message successfully stored.";
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

    public static int returnTotalMessages() {
        return sentMessages.size();
    }

    // ----- JSON file: save and read -----

    // Saves all the stored messages in the JSON file
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

        try (FileWriter writer = new FileWriter(storageFile)) {
            writer.write(json);
        } catch (IOException e) {
            System.out.println("Could not save the messages file.");
        }
    }

    // Reads the JSON file back into the stored messages array.
    // Call this once when the program starts.
    // The regex idea is based on: Oracle (n.d.) Lesson: Regular Expressions.
    public static void loadStoredMessages() {
        File file = new File(storageFile);
        if (!file.exists()) {
            return;
        }

        try {
            String json = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

            String field = "\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"";
            Pattern pattern = Pattern.compile(
                    "\"messageID\"" + field
                    + "\\s*,\\s*\"messageHash\"" + field
                    + "\\s*,\\s*\"recipient\"" + field
                    + "\\s*,\\s*\"message\"" + field);

            Matcher matcher = pattern.matcher(json);
            while (matcher.find()) {
                Message m = new Message(unclean(matcher.group(1)),
                        unclean(matcher.group(2)),
                        unclean(matcher.group(3)),
                        unclean(matcher.group(4)));
                populate(m, "Stored");
            }
        } catch (IOException e) {
            System.out.println("Could not read the messages file.");
        }
    }

    // Makes quotes and slashes safe to put inside JSON text
    private static String clean(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    // Turns JSON text back into normal text
    private static String unclean(String text) {
        return text.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    // ----- Part 3: working with the arrays -----

    public static void setSender(String name) {
        sender = name;
    }

    // Lets the unit tests use a different file so they don't touch messages.json
    public static void setStorageFile(String path) {
        storageFile = path;
    }

    public static ArrayList<String> sentMessageTexts() {
        ArrayList<String> texts = new ArrayList<>();
        for (Message m : sentMessages) {
            texts.add(m.messageText);
        }
        return texts;
    }

    public static int storedMessageCount() {
        return storedMessages.size();
    }

    public static int disregardedMessageCount() {
        return disregardedMessages.size();
    }

    public static ArrayList<String> getMessageHashes() {
        return new ArrayList<>(messageHashes);
    }

    public static ArrayList<String> getMessageIDs() {
        return new ArrayList<>(messageIDs);
    }

    // a) Sender and recipient of all the sent messages
    public static String displaySenderAndRecipient() {
        if (sentMessages.isEmpty()) {
            return "No sent messages.";
        }
        String result = "";
        for (Message m : sentMessages) {
            result = result + "Sender: " + sender + ", Recipient: " + m.recipient + "\n";
        }
        return result;
    }

    // b) The longest message (looks at the sent and stored messages)
    public static String displayLongestMessage() {
        String longest = "";
        for (Message m : sentMessages) {
            if (m.messageText.length() > longest.length()) {
                longest = m.messageText;
            }
        }
        for (Message m : storedMessages) {
            if (m.messageText.length() > longest.length()) {
                longest = m.messageText;
            }
        }
        if (longest.isEmpty()) {
            return "No messages found.";
        }
        return longest;
    }

    // c) Search for a message ID and show the recipient and message
    public static String searchByMessageID(String id) {
        ArrayList<Message> all = new ArrayList<>();
        all.addAll(sentMessages);
        all.addAll(storedMessages);
        all.addAll(disregardedMessages);

        for (Message m : all) {
            if (m.messageID.equals(id)) {
                return "\"" + m.recipient + "\", \"" + m.messageText + "\"";
            }
        }
        return "Message ID not found.";
    }

    // d) All the sent or stored messages for one recipient
    public static String searchByRecipient(String recipient) {
        ArrayList<Message> all = new ArrayList<>();
        all.addAll(sentMessages);
        all.addAll(storedMessages);

        String result = "";
        for (Message m : all) {
            if (m.recipient.equals(recipient)) {
                if (!result.isEmpty()) {
                    result = result + ", ";
                }
                result = result + "\"" + m.messageText + "\"";
            }
        }
        if (result.isEmpty()) {
            return "No messages found for that recipient.";
        }
        return result;
    }

    // e) Delete a message using its hash
    public static String deleteByHash(String hash) {
        Message removed = removeByHash(sentMessages, hash);

        if (removed == null) {
            removed = removeByHash(storedMessages, hash);
            if (removed != null) {
                storeMessage(); // update the JSON file
            }
        }
        if (removed == null) {
            removed = removeByHash(disregardedMessages, hash);
        }
        if (removed == null) {
            return "Message hash not found.";
        }

        messageHashes.remove(removed.messageHash);
        messageIDs.remove(removed.messageID);
        return "Message: \"" + removed.messageText + "\" successfully deleted.";
    }

    // Finds a message with this hash in a list and removes it
    private static Message removeByHash(ArrayList<Message> list, String hash) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).messageHash.equals(hash)) {
                return list.remove(i);
            }
        }
        return null;
    }

    // f) Report of all the sent messages
    public static String displayReport() {
        if (sentMessages.isEmpty()) {
            return "No sent messages.";
        }
        String result = "";
        for (Message m : sentMessages) {
            result = result + "Message Hash: " + m.messageHash + "\n"
                    + "Recipient: " + m.recipient + "\n"
                    + "Message: " + m.messageText + "\n\n";
        }
        return result;
    }

    // Clears everything (used by the unit tests so each test starts fresh)
    public static void resetMessages() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        numMessages = 0;
        sender = "Developer";
    }
}