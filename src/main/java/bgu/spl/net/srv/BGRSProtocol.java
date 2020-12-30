package bgu.spl.net.srv;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.Messages.LOGIN;
import bgu.spl.net.srv.Messages.Message;

public class BGRSProtocol<T> implements MessagingProtocol<Message> {
    private T arg;
    private String userName = "";
    public BGRSProtocol(T arg){
        this.arg = arg;
    }
    @Override
    public Message process(Message msg) {
        if(msg.needUserName()) // if we need to add the user name
            msg.AddUserName(userName);
        Message res = msg.execute(arg); // if username is empty then an ERR message will return
        if(res.getOpCode() == 12 && msg.isLogin()) // we logged in
            userName = ((LOGIN)msg).getUserName();// set the user name so we can use it in the next messages
        return res;
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}
