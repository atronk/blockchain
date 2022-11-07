package blockchain;

import java.io.Serializable;

public class Message implements Serializable {
    private final int amount;
    private final int id;
    private final Client sender;
    private final Client recipient;
    private byte[] signature;


    public Message(int id, Client sender, Client recipient, int amount) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    public Client getSender() {
        return sender;
    }

    public Client getRecipient() {
        return recipient;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return String.format("%s sent %d VC to %s",
                sender.getClientName(), amount, recipient.getClientName());
    }
}
