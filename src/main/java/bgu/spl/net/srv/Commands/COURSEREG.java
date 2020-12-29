package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;

public class COURSEREG implements Command<Database> {
    private final short opcode =5;
    private short courseNum;
    private String userName;

    public COURSEREG(short courseNum){
        this.courseNum = courseNum;

    }
    @Override
    public Serializable execute(Database arg) {
        //return arg.registerToCourse(userName,courseNum);
        if(arg.registerToCourse(userName,courseNum))
            return new ACK(opcode,"Registered to course : "+ courseNum +" successfully");
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
