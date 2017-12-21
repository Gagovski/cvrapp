package com.gc.cvrapp.cvr;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Base class for cvr connection
 */
public abstract class CvrConnection <E> {
    /**
     * close the cvr connection
     */
    public abstract void close();

    /**
     * cvr transfer out data
     * @param endpoint the transfer out endpoint
     * @param buffer the transfer out data buffer
     * @param length the transfer out data length
     * @param timeout the transfer out timeout
     * @return the out data size
     */
    public abstract int transferOut(@NonNull Object endpoint, @NonNull byte[] buffer, int length, int timeout);

    /**
     * cvr transfer in data
     * @param endpoint the transfer in endpoint
     * @param buffer the transfer in data buffer
     * @param maxLength the transfer in data length
     * @param timeout the transfer in timeout
     * @return the in data size
     */
    public abstract int transferIn(@NonNull Object endpoint, @NonNull byte[] buffer, int maxLength, int timeout);

    /**
     * cvr transfer in data
     * @param endpoint the transfer in endpoint
     * @param buffer the transfer in data buffer
     * @param maxLength the transfer in data length
     * @return the in data size
     */
    public abstract int transferIn(@NonNull Object endpoint, @NonNull byte[] buffer, int maxLength);

    /**
     * cvr get max packet size
     * @return the max out data size
     */
    public abstract int getMaxPacketOutSize();

    /**
     * cvr get max packet size
     * @return the max in data size
     */
    public abstract int getMaxPacketInSize();

    /**
     * cvr get out endpoint
     * @return the out endpoint object
     */
    public abstract @NonNull Object getOut();

    /**
     * cvr get in endpoint
     * @return the in endpoint object
     */
    public abstract Object getIn();

    /**
     * cvr get in endpoints
     * @return the in endpoint objects
     */
    public abstract @NonNull List<E> getIns();
}
