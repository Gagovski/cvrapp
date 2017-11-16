package com.gc.cvrapp.cvr.commands;

import com.gc.cvrapp.cvr.Cvr;
import com.gc.cvrapp.cvr.CvrSettings;
import com.gc.cvrapp.cvr.CvrConstants;
import com.gc.cvrapp.utils.LogUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SettingCommand extends Command {
    private static final String TAG = "SettingCommand";
    private ByteBuffer mPayload = ByteBuffer.allocate(8);

    public SettingCommand(Cvr camera, CvrSettings settings) {
        super(camera);
        mPayload.position(0);
        mPayload.order(ByteOrder.LITTLE_ENDIAN);
        LogUtil.i(TAG, "record time: " + settings.getRecordTime() + " voice_en: " + settings.getEnVoice());
        mPayload.putInt(settings.getRecordTime());
        mPayload.putInt(settings.getEnVoice());
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, CvrConstants.CommandCode.Setting, 0, 0, mPayload.capacity());
    }

    @Override
    public void exec(Cvr.CommandIO io) {
        io.handleCommand(this, mPayload.array(), 8);
    }

    @Override
    public void resp(Cvr.ResponseIO io, ByteBuffer resp, int payloadlen) {
        io.handleResponse(this, resp, payloadlen);
    }

    @Override
    public short code() {
        return CvrConstants.ResponseCode.Setting;
    }
}
