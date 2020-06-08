package Broker;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Topic {
    private String name;
    private Semaphore semaphore;
    private final Lock lock = new ReentrantLock();

    private File topicFile;
    private TopicWriter topicWriter;
    private HashMap<String, TopicReader> topicReaders;

    Topic(String name) {
        this.name = name;
        semaphore = new Semaphore(0);
        topicFile = new File(name + ".dat");
        topicWriter = new TopicWriter(this);
        topicReaders = new HashMap<>();
    }

    public File getTopicFile() {
        return topicFile;
    }

    public String getName() {
        return name;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public void setSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    private void addGroup(String groupName) {
        topicReaders.put(groupName, new TopicReader(this, groupName));
    }

    /**
     * This method is used to get the first value in the topic file which is not read in the given group yet, and serve it for the appropriate consumer.
     * @return the value of the first remained item.
     */
    public int get(String groupName, String consumerName) {
        synchronized (this) {
            if (!topicReaders.containsKey(groupName)) {
                addGroup(groupName);
            }
            return topicReaders.get(groupName).get(consumerName);
        }
    }

    /**
     * This method is used to put the given value at the tail of the topic file.
     * @param value the value to be put at the end of the topic file
     * @return Nothing.
     */
    public void put(String producerName, int value) {
        topicWriter.put(producerName, value);
    }



}
