package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;

public class ISREGISTERED implements Command<Database> {
    private String userName;
    private Integer courseNum;
    public ISREGISTERED(String userName,Integer courseNum){
        this.userName = userName;
        this.courseNum = courseNum;
    }
    @Override
    public Serializable execute(Database arg) {
        //return arg.isRegistered(userName,courseNum);
        if(arg.isRegistered(userName,courseNum))
            return "REGISTERED";
        return "NOT REGISTERED";
    }
}
