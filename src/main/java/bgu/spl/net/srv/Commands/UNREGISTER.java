package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;

public class UNREGISTER implements Command<Database> {
    private String userName;
    private Integer courseNum;
    public UNREGISTER(String userName,Integer courseNum){
        this.userName = userName;
        this.courseNum = courseNum;
    }
    @Override
    public Serializable execute(Database arg) {
        //return arg.unregister(userName,courseNum);
        if(arg.unregister(userName,courseNum))
            return new ACK();
        return new ERR();
    }
}
