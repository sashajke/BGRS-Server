package bgu.spl.net.srv.Messages;

import bgu.spl.net.srv.Database;

public class UNREGISTER implements Message<Database> {
    private final short opcode =10;
    private String userName;
    private short courseNum;
    public UNREGISTER(short courseNum){
        this.courseNum = courseNum;
    }
    @Override
    public Message execute(Database arg) {
        //return arg.unregister(userName,courseNum);
        if(arg.unregister(userName,courseNum))
            return new ACK(opcode,"");
        return new ERR(opcode);
    }

    @Override
    public short getOpCode() {
        return opcode;
    }

    @Override
    public void AddUserName(String userName) {
        this.userName = userName;
    }
    @Override
    public boolean needUserName() {
        return true;
    }
}
