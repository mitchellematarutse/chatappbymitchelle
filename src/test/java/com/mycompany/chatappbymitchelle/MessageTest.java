package com.mycompany.chatappbymitchelle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    // Start every test with a clean list of messages
    @BeforeEach
    public void setUp() {
        Message.resetMessages();
    }

    // ----- Test data from the brief -----
    // Message 1: +27718693002, "Hi Mike, can you join us for dinner tonight?", Send
    // Message 2: 08575975889, "Hi Keegan, did you receive the payment?", Discard

    // ----- assertEquals tests -----

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

    // Tests the hashes for both messages in a loop
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

    // ----- assertTrue test -----

    @Test
    public void testMessageIDLength() {
        Message m = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        assertTrue(m.checkMessageID());
    }
}