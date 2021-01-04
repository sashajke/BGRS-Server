package bgu.spl.net.srv;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.Messages.ERR;
import bgu.spl.net.srv.Messages.LOGIN;
import bgu.spl.net.srv.Messages.Message;

public class BGRSProtocol<T> implements MessagingProtocol<Message> {
    private final short AdminReg=1,StudentReg=2,Login = 3,Logout =4,ACK=12; // Magic numbers to avoid using constants inside the code
    private T arg;
    private String userName = "";
    private boolean shouldTerminate = false;
    public BGRSProtocol(T arg){
        this.arg = arg;
    }
    @Override
    public Message process(Message msg) {
        if(!userName.isEmpty())
        {
            // if someone is logged in you can't register from this client
            if(msg.getOpCode() == AdminReg || msg.getOpCode() == StudentReg || msg.getOpCode() == Login)
                return new ERR(msg.getOpCode());
        }
        msg.AddUserName(userName); // will add the username if needed
        Message res = msg.execute(arg); // if username is empty then an ERR message will return
        if(res.getOpCode() == ACK && msg.getOpCode() == Login) // we logged in
            userName = ((LOGIN)msg).getUserName();// set the user name so we can use it in the next messages
        if(res.getOpCode() == ACK && msg.getOpCode() == Logout) // we logged out
            shouldTerminate = true;
        
        return res;
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
