package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;

public class COURSEREG implements Command<Database> {
    private Integer courseNum;
    private String userName;

    public COURSEREG(Integer courseNum,String userName){
        this.courseNum = courseNum;
        this.userName = userName;
    }
    @Override
    public Serializable execute(Database arg) {
        //return arg.registerToCourse(userName,courseNum);
        if(arg.registerToCourse(userName,courseNum))
            return new ACK();
        return new ERR();
    }
}
