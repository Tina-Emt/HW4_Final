package Broker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class TopicWriter {

    RandomAccessFile buffer;
    private Topic topic;
    private HashMap<String, Transaction> transactions;


    TopicWriter(Topic topic) {
        this.topic=topic;
        transactions = new HashMap<>();
        try {
            buffer = new RandomAccessFile(topic.getTopicFile().getPath(), "rw");

            buffer.seek(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void put(String producerName, int value) {
        if(value <= 0) {
            handleTransactionOperation(producerName, value);
        }
        else {
            handleInsertOperation(producerName, value);
        }
    }

    private void handleTransactionOperation(String producerName, int value) {
        switch (value) {
            case 0:
                startTransaction(producerName);
                break;
            case -1:
                commitTransaction(producerName);
                break;
            case -2:
                cancelTransaction(producerName);
        }
    }

    private void handleInsertOperation(String producerName, int value) {
        if(transactions.containsKey(producerName)) {
            transactions.get(producerName).put(value);
        }
        else {
            synchronized (buffer){
                writeValue(value);
            }

        }
    }

    private void addTransaction(String producerName) {
        transactions.put(producerName, new Transaction(this, producerName));
    }

    /**
     * This method is used to start a transaction for putting a transaction of values inside the buffer.
     * @return Nothing.
     */
    private void startTransaction(String producerName) {
        if(transactions.containsKey(producerName)) {
            //To Do - Log the problem in finalizing previous transaction.   Done !
            Util.LoggerClass.getInstance().getLogger().severe("Error : Problem in finalizing previous transaction.");
            commitTransaction(producerName);
            transactions.remove(producerName);
        }
        addTransaction(producerName);
    }

    /**
     * This method is used to end the transaction for putting a its values inside the file.
     * @return Nothing.
     */
    private void commitTransaction(String producerName) {
        if(transactions.containsKey(producerName)) {
            synchronized (buffer){
                transactions.get(producerName).commit();
                transactions.remove(producerName);
            }

        }
        else {
            //To Do - Log the problem in committing a non-existing transaction.    Done!
            Util.LoggerClass.getInstance().getLogger().severe("Error : Problem in committing a non existing transaction.");

        }
    }

    /**
     * This method is used to cancel a transaction.
     * @return Nothing.
     */
    private void cancelTransaction(String producerName) {
        if(transactions.containsKey(producerName)) {
            transactions.remove(producerName);
        }
        else {
            //To Do - Log the problem in canceling a non-existing transaction.  Done!
            Util.LoggerClass.getInstance().getLogger().severe("Error : Problem in committing a non existing transaction.");

        }
    }

    public void writeValue(int value) {
        //To Do - Put the given value at the end of the topicFile   Done!
        try {
            synchronized (this) {
                buffer.writeInt(value);
                topic.getSemaphore().release();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
