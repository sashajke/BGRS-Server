package bgu.spl.net.srv.Messages;

import bgu.spl.net.srv.Database;

public class ACK implements Message<Database> {
    private final short opcode =12;
    private short messageOpcode;
    private String attachment;
    public ACK(short messageOpcode,String attachment){
        this.messageOpcode = messageOpcode;
        this.attachment = attachment;
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
    public String getAttachment(){
        return attachment;
    }
}
