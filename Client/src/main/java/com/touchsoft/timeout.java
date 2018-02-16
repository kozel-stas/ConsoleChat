package com.touchsoft;

import java.util.TimerTask;

public class timeout extends TimerTask{
    private User user;

    public timeout(User user){
        this.user=user;
    }

    public void run() {
        if(user.checkTimeout()==true)
            System.exit(0);
    }
}
