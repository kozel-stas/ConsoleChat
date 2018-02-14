package com.touchsoft;

import java.util.Date;

public class Client {
        private String name;
        private boolean status; //реализовтаь при сохранении агентов и юзеров

        protected Client() {
        }

        public Client(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

class User extends com.touchsoft.Client {
        private Date date = new Date();
        private long last_activity;
        private long timeout = 300000;

        public User(String name) {
            super(name);
            last_activity = date.getTime();
        }

        public boolean checkTimeout() {
            if (last_activity + timeout < date.getTime()) return true;
            else return false;
        }

        public void updateTimeout() {
            last_activity = date.getTime();
        }

    }

class Agent extends com.touchsoft.Client {

        public Agent(String name) {
            super(name);
        }
}


