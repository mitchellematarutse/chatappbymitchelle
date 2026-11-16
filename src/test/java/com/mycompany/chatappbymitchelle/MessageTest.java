package com.mycompany.chatappbymitchelle;

import java.io.File;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    // Start every test with empty arrays and a test JSON file
    @BeforeEach
    public void setUp() {
        Message.resetMessages();
        Message.setStorageFile("target/test-messages.json");
        new File("target/test-messages.json").delete();
    }

    // Fills the arrays with the 5 test messages from the brief
    private void loadTestData() {
        Message.populate(new Message("0000000001", 0, "+27834557896",
                "Did you get the cake?"), "Sent");
        Message.populate(new Message("0000000002", 1, "+27838884567",
                "Where are you? You are late! I have asked you to be on time."), "Stored");
        Message.populate(new Message("0000000003", 2, "+27834484567",
                "Yohoooo, I am at your gate."), "Disregard");
        Message.populate(new Message("0000000004", 3, "0838884567",
                "It is dinner time !"), "Sent");
        Message.populate(new Message("0000000005", 4, "+27838884567",
                "Ok, I am leaving without you."), "Stored");
    }

    // ===== Part 2 tests =====

    @Test
    public void testMessageLengthSuccess() {
        assertEquals("Message ready to send.",
                Message.checkMessageLength("Hi Mike, can you join us for dinner tonight?"));
    }

    @Test
    public void testMessageLengthFailure() {
        String longMessage = "a".repeat(260);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                Message.checkMessageLength(longMessage));
    }

    @Test
    public void testRecipientSuccess() {
        assertEquals("Cell phone number successfully captured.",
                Message.checkRecipientCell("+27718693002"));
    }

    @Test
    public void testRecipientFailure() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                Message.checkRecipientCell("08575975889"));
    }

    @Test
    public void testMessageHashMessage1() {
        Message m = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        assertEquals("00:0:HITONIGHT", m.createMessageHash());
    }

    @Test
    public void testMessageHashesInLoop() {
        String[] ids = {"0012345678", "0098765432"};
        int[] numbers = {0, 1};
        String[] recipients = {"+27718693002", "08575975889"};
        String[] texts = {
            "Hi Mike, can you join us for dinner tonight?",
            "Hi Keegan, did you receive the payment?"
        };
        String[] expected = {"00:0:HITONIGHT", "00:1:HIPAYMENT"};

        for (int i = 0; i < texts.length; i++) {
            Message m = new Message(ids[i], numbers[i], recipients[i], texts[i]);
            assertEquals(expected[i], m.createMessageHash());
        }
    }

    @Test
    public void testMessageIDCreated() {
        Message m = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message ID generated: 0012345678", m.messageIDGenerated());
    }

    @Test
    public void testSendMessage() {
        Message m = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message successfully sent.", m.sentMessage(1));
    }

    @Test
    public void testDisregardMessage() {
        Message m = new Message("0098765432", 1, "08575975889",
                "Hi Keegan, did you receive the payment?");
        assertEquals("Press 0 to delete the message.", m.sentMessage(2));
    }

    @Test
    public void testStoreMessage() {
        Message m = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message successfully stored.", m.sentMessage(3));
    }

    @Test
    public void testTotalMessagesSent() {
        Message m1 = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        Message m2 = new Message("0098765432", 1, "08575975889",
                "Hi Keegan, did you receive the payment?");
        m1.sentMessage(1); // sent
        m2.sentMessage(2); // disregarded
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    public void testMessageIDLength() {
        Message m = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        assertTrue(m.checkMessageID());
    }

    // ===== Part 3 tests =====

    @Test
    public void testSentMessagesArrayPopulated() {
        loadTestData();
        assertEquals(Arrays.asList("Did you get the cake?", "It is dinner time !"),
                Message.sentMessageTexts());
    }

    @Test
    public void testAllArraysPopulated() {
        loadTestData();
        assertEquals(2, Message.sentMessageTexts().size());
        assertEquals(2, Message.storedMessageCount());
        assertEquals(1, Message.disregardedMessageCount());
        assertEquals(5, Message.getMessageHashes().size());
        assertEquals(5, Message.getMessageIDs().size());
    }

    @Test
    public void testStoredMessagesReadFromJson() {
        Message m = new Message("0000000002", 1, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        m.sentMessage(3);              // stores it and writes the JSON file
        Message.resetMessages();       // empty the arrays
        Message.loadStoredMessages();  // read the JSON file back into the array

        assertEquals(1, Message.storedMessageCount());
        assertEquals("\"Where are you? You are late! I have asked you to be on time.\"",
                Message.searchByRecipient("+27838884567"));
    }

    @Test
    public void testDisplayLongestMessage() {
        loadTestData();
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                Message.displayLongestMessage());
    }

    @Test
    public void testSearchByMessageID() {
        loadTestData();
        assertEquals("\"0838884567\", \"It is dinner time !\"",
                Message.searchByMessageID("0000000004"));
    }

    @Test
    public void testSearchByRecipient() {
        loadTestData();
        assertEquals("\"Where are you? You are late! I have asked you to be on time.\", \"Ok, I am leaving without you.\"",
                Message.searchByRecipient("+27838884567"));
    }

    @Test
    public void testDeleteByHash() {
        loadTestData();
        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.",
                Message.deleteByHash("00:1:WHERETIME"));
        assertEquals(1, Message.storedMessageCount());
    }

    @Test
    public void testDisplayReport() {
        loadTestData();
        String expected = "Message Hash: 00:0:DIDCAKE\n"
                + "Recipient: +27834557896\n"
                + "Message: Did you get the cake?\n\n"
                + "Message Hash: 00:3:ITTIME\n"
                + "Recipient: 0838884567\n"
                + "Message: It is dinner time !\n\n";
        assertEquals(expected, Message.displayReport());
    }
}