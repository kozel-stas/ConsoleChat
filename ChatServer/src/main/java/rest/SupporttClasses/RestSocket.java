package rest.SupporttClasses;

import model.ChatInterface;
import model.CommandContainer;

import java.util.ArrayList;
import java.util.List;

public class RestSocket implements ChatInterface {
    private List<CommandContainer> messages=new ArrayList<>();

    @Override
    public void send(CommandContainer commandContainer) {
        messages.add(commandContainer);
    }

    @Override
    public void close() {
        messages.clear();
    }

    public List<CommandContainer> getMessages(int page, int numberMsgInPage){
        if(page*numberMsgInPage<messages.size()){
            return messages.subList(page*numberMsgInPage,(page+1)*numberMsgInPage>messages.size()?messages.size():(page+1)*numberMsgInPage);
        } return null;
    }
}
