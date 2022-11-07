package blockchain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class BlockchainUser extends Miner implements Debuggable, Callable<Block> {

    public BlockchainUser(int id, String name) {
        super(name, id);
    }

    public static List<BlockchainUser> getMaxMinersClients() {
        int threads = Runtime.getRuntime().availableProcessors();
        List<BlockchainUser> miners = new ArrayList<>();

        for (int i = 1; i <= threads; i++) {
            miners.add(new BlockchainUser(i, "miner" + i));
        }

        return miners;
    }

    @Override
    public Block call() {
        logger.info(String.format("Miner %d begins to make a block with id %d%n\n",
                this.id, blockchain.getLastId() + 1));
        return mineBlock();
    }
}
