package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;

public class ERR implements Command<Database> {
    private final short opcode =13;
    private short messageOpcode;
    public ERR(short messageOpcode){
        this.messageOpcode = messageOpcode;
    }
    @Override
    public Serializable execute(Database arg) {
        return null;
    }

    @Override
    public short getOpCode() {
        return opcode;
    }
}
