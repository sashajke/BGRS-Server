package bgu.spl.net.impl.rci;

import java.awt.*;
import java.io.Serializable;

public interface Command<T> extends Serializable {
    Serializable execute(T arg);
    short  getOpCode();
    default void AddUserName(String userName){}
    default boolean needUserName(){return false;}
    default boolean isLogin(){return false;}
}
