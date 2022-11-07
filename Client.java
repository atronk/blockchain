package blockchain;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Client implements Debuggable, Serializable {
    protected static final Blockchain blockchain = Blockchain.blockchain();
    private static final List<Client> clients = new ArrayList<>();
    protected final String name;
    protected final int id;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private transient Thread thread = null;

    public Client(String name, int id) {
        this.name = name;
        this.id = id;
        setKeys();
        clients.add(this);
    }

    private void setKeys() {
        Encryption gk = Encryption.getGenerator();
        gk.createKeys();
        this.privateKey = gk.getPrivateKey();
        this.publicKey = gk.getPublicKey();
    }

    private Client randomClient() {
        Random r = ThreadLocalRandom.current();
        int index = r.nextInt(0, clients.size());
        while (clients.get(index).equals(this)) {
            index = r.nextInt(0, clients.size());
        }
        return clients.get(index);
    }

    private int randomAmountToSend() {
        Random r = ThreadLocalRandom.current();
        return r.nextInt(1, 50);
    }

    private void sendMessage() {
        synchronized (blockchain.messagesMutex) {
            try {
                Message message = new Message(
                        blockchain.getLastMessageId() + 1,
                        this,
                        randomClient(),
                        randomAmountToSend());
                message.setSignature(Encryption.sign(message.toString(), privateKey));
                blockchain.addMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public String getClientName() {
        return this.name;
    }

    private void _startSendingMessages() {
        Random r = ThreadLocalRandom.current();
        while (!Thread.currentThread().isInterrupted()) {
            try {
                logger.info(String.format("Client %s is sending a message", this.name));
                sendMessage();
                logger.info(String.format("Client %s is waiting", this.name));
                Thread.sleep(r.nextInt(1_000, 6_000));
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void startSendingMessages() {
        this.thread = new Thread(this::_startSendingMessages);
        this.thread.start();
    }

    public void interrupt() {
        this.thread.interrupt();
        this.thread = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return id == client.id && Objects.equals(name, client.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}
