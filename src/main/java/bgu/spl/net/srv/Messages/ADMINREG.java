package bgu.spl.net.srv.Messages;

import bgu.spl.net.srv.Database;


public class ADMINREG implements Message<Database> {
    private final short opcode = 1;
    private String userName;
    private String password;
    public ADMINREG(String userName,String password){
        this.userName = userName;
        this.password = password;
    }
    @Override
    public Message execute(Database arg) {
        //return arg.adminRegister(userName,password);
        if(arg.adminRegister(userName,password))
            return new ACK(opcode,"");
        return new ERR(opcode);
    }

    @Override
    public short getOpCode() {
        return opcode;
    }
}
