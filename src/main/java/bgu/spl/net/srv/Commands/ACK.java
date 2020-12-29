package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;

public class ACK implements Command<Database> {
    private final short opcode =12;
    private short messageOpcode;
    private String attachment;
    public ACK(short messageOpcode,String attachment){
        this.messageOpcode = messageOpcode;
        this.attachment = attachment;
    }
    @Override
    public Serializable execute(Database arg) {
        return null;
    }

    @Override
    public short getOpCode() {
        return opcode;
    }
}
