package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;

public class UNREGISTER implements Command<Database> {
    private final short opcode =10;
    private String userName;
    private short courseNum;
    public UNREGISTER(short courseNum){
        this.courseNum = courseNum;
    }
    @Override
    public Serializable execute(Database arg) {
        //return arg.unregister(userName,courseNum);
        if(arg.unregister(userName,courseNum))
            return new ACK(opcode,"Unregistered successfully");
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
