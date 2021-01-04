package bgu.spl.net.srv.Messages;

import bgu.spl.net.srv.Database;

public class ISREGISTERED implements Message<Database> {
    private final short opcode =9;
    private String userName;
    private short courseNum;
    public ISREGISTERED(short courseNum){
        this.courseNum = courseNum;
    }
    @Override
    public Message execute(Database arg) {
        //return arg.isRegistered(userName,courseNum);
        if(arg.isRegistered(userName,courseNum))
            return new ACK(opcode,"REGISTERED");
        return new ACK(opcode,"NOT REGISTERED");
    }

    @Override
    public short getOpCode() {
        return 0;
    }

    @Override
    public void AddUserName(String userName) {
        this.userName = userName;
    }
}
