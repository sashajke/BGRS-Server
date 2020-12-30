package bgu.spl.net.srv.Messages;

public interface Message<T> {
    Message execute(T arg);
    short  getOpCode();
    default void AddUserName(String userName){}
    default boolean needUserName(){return false;}
    default boolean isLogin(){return false;}
    default short getMessageOpcode(){return -1;}
}
