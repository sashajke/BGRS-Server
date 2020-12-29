package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;

public class STUDENTREG implements Command<Database> {
    private final short opcode =2;
    private String userName;
    private String password;
    public STUDENTREG(String userName,String password){
        this.userName = userName;
        this.password = password;
    }
    @Override
    public Serializable execute(Database arg) {
        //return arg.studentRegister(userName,password);
        if(arg.studentRegister(userName,password))
            return new ACK(opcode,"Registered Successfully");
        return new ERR(opcode);
    }

    @Override
    public short getOpCode() {
        return opcode;
    }
}
