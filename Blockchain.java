package blockchain;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class Blockchain implements Debuggable {
    private static final Blockchain thisInstance = new Blockchain();
    private static final int STARTING_BALANCE = 100;
    private static final int MINING_REWARD = 100;
    public final Object messagesMutex = new Object();
    private final List<Message> messages = new ArrayList<>();
    private LinkedList<Block> blocks = new LinkedList<>();
    private int zeroNum = 1;
    private int maxBlocks = 5;

    private Blockchain() {
    }

    public static Blockchain blockchain() {
        return thisInstance;
    }

    public synchronized boolean addBlock(Block block) {
        if (validateBlock(block)) {
            synchronized (messagesMutex) {
                block.setMessages(messages);
                messages.clear();
                blocks.add(block);
            }
            if (block.getCreationDuration() == 0) {
                if (this.zeroNum < 5) // to finish the project
                    this.zeroNum += 1;
                block.setNDelta(1);
                logger.log(Level.INFO, "Increased num of zero");
            } else if (block.getCreationDuration() > 1) {
                this.zeroNum -= 1;
                block.setNDelta(-1);
                logger.log(Level.INFO, "Decreased num of zero");
            } else {
                block.setNDelta(0);
                logger.log(Level.INFO, "Zeroes are the same");
            }
            return true;
        }
        return false;
    }

    public synchronized String getLastBlockHash() {
        if (!blocks.isEmpty()) {
            return blocks.peekLast().getThisHash();
        }
        return "0";
    }

    public synchronized long getLastId() {
        if (!blocks.isEmpty()) {
            return blocks.peekLast().getId();
        }
        return 0L;
    }

    public int getLastMessageId() {
        return messages.size();
    }

    public int getZeroNum() {
        return this.zeroNum;
    }

    public BlockchainState getState() {
        return new BlockchainState(blocks, zeroNum);
    }

    public void setState(BlockchainState state) {
        this.blocks = state.blocks;
        this.zeroNum = state.zeroNum;
    }

    public int getSize() {
        return this.blocks.size();
    }

    public int getMaxBlocks() {
        return maxBlocks;
    }

    public void setMaxBlocks(int maxBlocks) {
        this.maxBlocks = maxBlocks;
    }

    private int getClientBalance(Client c) {
        synchronized (messagesMutex) {
            int balance = STARTING_BALANCE;
            for (Block block : blocks) {
                if (block.getCreator().equals(c)) {
                    balance += MINING_REWARD;
                }
                for (Message message : block.getMessages()) {
                    if (message.getSender().equals(c)) {
                        balance -= message.getAmount();
                    }
                    if (message.getRecipient().equals(c)) {
                        balance += message.getAmount();
                    }
                }
            }
            for (Message message : messages) {
                if (message.getSender().equals(c)) {
                    balance -= message.getAmount();
                }
                if (message.getRecipient().equals(c)) {
                    balance += message.getAmount();
                }
            }
            return balance;
        }
    }

    public void addMessage(Message msg) {
        if (validateMessage(msg)) {
            logger.info(String.format("message id %d is valid", msg.getId()));
            this.messages.add(msg);
        } else {
            logger.info(String.format("message id %d is discarded", msg.getId()));
        }
    }

    public boolean validateBlock(Block block) {
        return this.getLastBlockHash().equals(block.getPreviousBlockHash());
    }

    public boolean validateMessage(Message msg) {
        try {
            if (!(msg.getId() > getLastMessageId())) {
                return false;
            }
            if (!Encryption.verifySignature(msg.toString(), msg.getSignature(), msg.getSender().getPublicKey())) {
                return false;
            }
            int senderBalance = getClientBalance(msg.getSender());
            logger.info(String.format("sender %s(%d) sends %d to %s",
                    msg.getSender().getClientName(),
                    senderBalance, msg.getAmount(),
                    msg.getRecipient().getClientName()));
            return senderBalance >= msg.getAmount();
        } catch (Exception e) {
            return false;
        }
    }

    public void print() {
        for (Block block : blocks) {
            System.out.println(block);
        }
    }

    public void print(int n) {
        for (int i = 0; i < n; i++) {
            System.out.println(blocks.get(i));
        }
    }

    static class BlockchainState implements Serializable {

        @Serial
        final private static long serialVersionUID = 1L;
        final private LinkedList<Block> blocks;
        final private Integer zeroNum;


        BlockchainState(LinkedList<Block> blocks, Integer zeroNum) {
            this.blocks = new LinkedList<>(blocks);
            this.zeroNum = zeroNum;
        }
    }
}
