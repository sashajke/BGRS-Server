package bgu.spl.net.srv.Messages;

import bgu.spl.net.srv.Database;

public class COURSEREG implements Message<Database> {
    private final short opcode =5;
    private short courseNum;
    private String userName;

    public COURSEREG(short courseNum){
        this.courseNum = courseNum;

    }
    @Override
    public Message execute(Database arg) {
        //return arg.registerToCourse(userName,courseNum);
        String toReturn = "Registered to course : "+ courseNum +" successfully"; // should add to the ack
        if(arg.registerToCourse(userName,courseNum))
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
}
