package model;

public class MessageWeb {
    private String name;
    private String msg;

    public String getName() {
        return name;
    }

    public String getMsg() {
        return msg;
    }

    public MessageWeb(String name, String msg){
        this.msg=msg;
        this.name=name;
    }
}
