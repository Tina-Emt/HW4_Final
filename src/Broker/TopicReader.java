package Broker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Semaphore;

public class TopicReader {

    RandomAccessFile topicFile;
    Semaphore semaphore;

    private Topic topic;
    private String groupName;
    String currentTransaction = "None";


    TopicReader(Topic topic, String groupName) {
        this.topic = topic;
        this.groupName=groupName;
        //To Do - Generate topicFile    Done!
        try {
            topicFile = new RandomAccessFile(topic.getTopicFile().getPath(), "rw");
            topicFile.seek(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        semaphore = new Semaphore(1);
    }

    public int get(String consumerName) {
        int value = 0;

        if (currentTransaction.equals("None") || !consumerName.equals(currentTransaction)) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {

            }
        }

        /*while (true) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (currentTransaction.equals("None") || consumerName.equals(currentTransaction)) {
                break;
            } else {
                this.notify();
            }
        }

        if (!consumerName.equals(currentTransaction)) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } */

        try {

                try {
                    topic.getSemaphore().acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                value = topicFile.readInt();

                switch (value) {
                    case 0:
                        this.currentTransaction = consumerName;
                /*try {
                    this.semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                        break;

                    case -1:
                        this.currentTransaction = "None";
                        this.semaphore.release();
                        break;

                    default:
                        if (currentTransaction.equals("None")) {
                            semaphore.release();
                        }
                        break;
                }




        } catch (IOException e) {

            e.printStackTrace();
        }

        return value;
    }


}
