package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KDAMCHECK implements Command<Database> {
    private Integer courseNum;
    public KDAMCHECK(Integer courseNum)
    {
        this.courseNum = courseNum;
    }
    @Override
    public Serializable execute(Database arg) {
        List<Integer> kdams = arg.getKdamCourses(courseNum);
        return new ArrayList<>(kdams);
    }
}
