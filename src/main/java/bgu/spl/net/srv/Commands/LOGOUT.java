package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;

public class LOGOUT implements Command<Database> {
    private final short opcode =4;
    private String userName;
    @Override
    public Serializable execute(Database arg) {
        if(arg.Logout(userName))
            return new ACK(opcode,"Logged out successfully");
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
