package blockchain;

public class Main {
    public static void main(String[] args) {
        BlockchainManager mng = new BlockchainManager();
        mng.needBlocks(15);

        mng.spamMessages();
        mng.addBlocks();
        mng.stopMessages();

        mng.print();
    }
}
