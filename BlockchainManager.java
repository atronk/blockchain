package blockchain;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class BlockchainManager implements Debuggable {
    private final String dataFileName = "./data/blockchain.data";
    private final Blockchain blockchain = Blockchain.blockchain();
    private final List<BlockchainUser> users = BlockchainUser.getMaxMinersClients();

    BlockchainManager() {
        turnDebugOff();
        if (FileUtils.isExistingFile(dataFileName)) {
            logger.info("File exists, loading from disk");
            Blockchain.BlockchainState state = load();
//            if (state != null) {              done to finish the project
//                blockchain.setState(state);
//            }
            logger.info(String.format("Blockchain has %d blocks, last hash: %s",
                    blockchain.getSize(), blockchain.getLastBlockHash()));
        } else {
            logger.info("File does not exist, creating a new blockchain");
            if (FileUtils.createFile(dataFileName)) {
                logger.info("Created directory and file");
            } else {
                logger.info("Could not create file to save blockchain");
                System.exit(1);
            }
        }
    }

    public static void shutdownExecutor(ExecutorService e) {
        try {
            logger.info("Shutting down executors...");
            e.shutdownNow();
            if (!e.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                throw new TimeoutException();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private synchronized void save() {
        Blockchain.BlockchainState state = blockchain.getState();
        try {
            SerializationUtils.serialize(state, dataFileName);
        } catch (IOException e) {
            logger.severe(String.format("Serialization error: %s\n", e.getMessage()));
            e.printStackTrace();
        }
    }

    private Blockchain.BlockchainState load() {
        try {
            return (Blockchain.BlockchainState) SerializationUtils.deserialize(dataFileName);
        } catch (IOException e) {
            logger.severe(String.format("%s. Deserialization error: %s\n", e, e.getMessage()));
        } catch (ClassNotFoundException e) {
            logger.severe(String.format("%s. Class not found: %s\n", e, e.getMessage()));
        }
        return null;
    }

    public void needBlocks(int amount) {
        this.blockchain.setMaxBlocks(amount);
    }

    public void addBlocks() {
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Callable<Block>> tasks = users.stream()
                .map(Miner::getCreateBlockCallable)
                .collect(Collectors.toList());

        logger.info(String.format("Running %d tasks to create %d blocks",
                tasks.size(), blockchain.getMaxBlocks()));
        logger.info("Starting mining blocks...");
        for (int i = 0; i < blockchain.getMaxBlocks(); i++) {
            try {
                Block b = executor.invokeAny(tasks);
                logger.info("Block was created!");
                if (blockchain.addBlock(b)) {
                    logger.info(String.format("%s added block with id %d, and hash %s",
                            b.getCreator(), b.getId(), b.getThisHash()));
                    save();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        shutdownExecutor(executor);
    }

    public void spamMessages() {
        for (Client client : users) {
            client.startSendingMessages();
        }
    }

    public void stopMessages() {
        for (Client client : users) {
            client.interrupt();
        }
    }

    public void print() {
        blockchain.print();
    }
}
