package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;

public class LOGIN implements Command<Database> {
    private String userName;
    private String password;
    public LOGIN(String userName,String password){
        this.userName = userName;
        this.password = password;
    }
    @Override
    public Serializable execute(Database arg) {
        //return arg.Login(userName,password);
        if(arg.Login(userName,password))
            return new ACK();
        return new ERR();
    }
}
