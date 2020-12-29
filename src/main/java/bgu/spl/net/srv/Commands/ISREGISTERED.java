package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;

public class ISREGISTERED implements Command<Database> {
    private final short opcode =9;
    private String userName;
    private short courseNum;
    public ISREGISTERED(short courseNum){
        this.courseNum = courseNum;
    }
    @Override
    public Serializable execute(Database arg) {
        //return arg.isRegistered(userName,courseNum);
        if(arg.isRegistered(userName,courseNum))
            return "REGISTERED";
        return "NOT REGISTERED";
    }

    @Override
    public short getOpCode() {
        return 0;
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
