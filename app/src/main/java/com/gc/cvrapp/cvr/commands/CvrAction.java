package com.gc.cvrapp.cvr.commands;


import com.gc.cvrapp.cvr.Cvr;

import java.nio.ByteBuffer;
/**
 * class for cvr message action.
 */
public interface CvrAction {

    /**
     * command execute action
     */
    void exec(Cvr.CommandIO io);

    /**
     * command response action
     */
    void resp(Cvr.ResponseIO io, ByteBuffer resp, int payloadlen);

    /**
     * command code get
     * @return command code
     */
    short code();

    /**
     * command execute's state
     * @return command execute state
     */
    boolean complete();
}
