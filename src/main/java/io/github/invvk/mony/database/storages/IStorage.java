package io.github.invvk.mony.database.storages;

public interface IStorage {

    /**
     * initial the connection
     */
    void init();

    /**
     * Close the connection
     */
    void close();

}
