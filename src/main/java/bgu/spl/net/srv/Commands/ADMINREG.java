package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;


public class ADMINREG implements Command<Database> {
    private final short opcode = 1;
    private String userName;
    private String password;
    public ADMINREG(String userName,String password){
        this.userName = userName;
        this.password = password;
    }
    @Override
    public Serializable execute(Database arg) {
        //return arg.adminRegister(userName,password);
        if(arg.adminRegister(userName,password))
            return new ACK(opcode,"Registered Successfully");
        return new ERR(opcode);
    }

    @Override
    public short getOpCode() {
        return opcode;
    }
}
