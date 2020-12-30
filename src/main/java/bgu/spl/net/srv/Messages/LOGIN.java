package bgu.spl.net.srv.Messages;

import bgu.spl.net.srv.Database;

public class LOGIN implements Message<Database> {
    private final short opcode =3;
    private String userName;
    private String password;
    public LOGIN(String userName,String password){
        this.userName = userName;
        this.password = password;
    }
    @Override
    public Message execute(Database arg) {
        //return arg.Login(userName,password);
        if(arg.Login(userName,password))
            return new ACK(opcode,"Logged in successfully");
        return new ERR(opcode);
    }

    @Override
    public short getOpCode() {
        return opcode;
    }

    @Override
    public boolean isLogin() {
        return true;
    }
    public String getUserName(){
        return userName;
    }
}
