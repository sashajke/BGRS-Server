package bgu.spl.net.srv.Messages;

import bgu.spl.net.srv.Database;

public class ERR implements Message<Database> {
    private final short opcode =13;
    private short messageOpcode;
    public ERR(short messageOpcode){
        this.messageOpcode = messageOpcode;
    }
    @Override
    public Message execute(Database arg) {
        return null;
    }

    @Override
    public short getOpCode() {
        return opcode;
    }

    @Override
    public short getMessageOpcode() {
        return messageOpcode;
    }
}
