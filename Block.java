package blockchain;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Block implements Serializable {
    @Serial
    final private static long serialVersionUID = 1L;
    final private long id;
    final private long timestamp;
    final private String previousBlockHash;
    final private String thisBlockHash;
    private Client creator;
    private long magicNumberForProof;
    private long creationDuration;
    private int nDelta;
    private List<Message> messages;

    public Block(long id, long timestamp, String oldHash, String proofOfWork) {
        this.id = id;
        this.timestamp = timestamp;
        this.previousBlockHash = oldHash;
        this.thisBlockHash = proofOfWork;
    }

    public String getThisHash() {
        return thisBlockHash;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public long getId() {
        return this.id;
    }

    public void setMagicNumberForProof(long magicNumber) {
        this.magicNumberForProof = magicNumber;
    }

    public long getCreationDuration() {
        return this.creationDuration;
    }

    public void setCreationDuration(long duration) {
        this.creationDuration = duration;
    }

    public Client getCreator() {
        return creator;
    }

    public void setCreator(Client miner) {
        this.creator = miner;
    }

    public List<Message> getMessages() {
        return this.messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = new ArrayList<>(messages);
    }

    public void setNDelta(int n) {
        this.nDelta = n;
    }

    @Override
    public String toString() {
        String data = messages.isEmpty()
                ? "no transactions"
                : messages.stream()
                .map(Message::toString)
                .collect(Collectors.joining("\n"));
        String delta = nDelta == 1
                ? "was increased by 1"
                : nDelta == -1
                ? "was decreased by 1"
                : "stays the same";
        return String.format("""
                        Block:
                        Created by %s
                        %s gets 100 VC
                        Id: %d
                        Timestamp: %d
                        Magic number: %d
                        Hash of the previous block:
                        %s
                        Hash of the block:
                        %s
                        Block data:
                        %s
                        Block was generating for %d seconds
                        N %s
                        """,
                creator.getClientName(), creator.getClientName(), id, timestamp, magicNumberForProof, previousBlockHash, thisBlockHash,
                data, creationDuration, delta);
    }
}