package blockchain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Miner extends Client {
    public Miner(String name, int id) {
        super(name, id);
    }

    public Block mineBlock() {
        Random r = ThreadLocalRandom.current();

        String lastHash = blockchain.getLastBlockHash();
        long id = blockchain.getLastId() + 1;
        Instant start = Instant.now();

        long magic = r.nextLong();
        String hashed = StringUtil.hashValues(lastHash, start, id, this.id, magic);
        logger.info(String.format("creating a block with id %d, oldHash %s, and %d zeroes",
                id, lastHash, blockchain.getZeroNum()));
        for (String zeroes = "0".repeat(Math.max(0, blockchain.getZeroNum()));
             !hashed.startsWith(zeroes); ) {
            if (Thread.currentThread().isInterrupted()) {
                logger.info(String.format("Thread #%d was interrupted", this.id));
                break;
            }
            magic = r.nextLong();
            hashed = StringUtil.hashValues(lastHash, start, id, this.id, magic);
        }
        long creationDuration = start.until(Instant.now(), ChronoUnit.SECONDS);

        Block block = new Block(id, start.toEpochMilli(), lastHash, hashed);
        block.setCreator(this);
        block.setCreationDuration(creationDuration);
        block.setMagicNumberForProof(magic);
        return block;
    }

    public Callable<Block> getCreateBlockCallable() {
        return this::mineBlock;
    }
}

