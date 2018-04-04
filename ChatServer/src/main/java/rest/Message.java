package rest;

import com.google.gson.Gson;
import model.*;
import model.SupportClasses.AnswerCode;
import model.SupportClasses.CommandContainer;
import model.SupportClasses.TypeApp;
import model.User;
import rest.SupporttClasses.PostMessageRequest;
import rest.SupporttClasses.RestSocket;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/message")
public class Message {
    private static Gson json = new Gson();
    private static DataManipulate dataManipulate = DataManipulate.getInstance();

    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String sendMsg_JSON(String msg) {
        PostMessageRequest request = json.fromJson(msg, PostMessageRequest.class);
        if (request.getRole() != null && request.getLogin() != null && request.getMsg() != null) {
            User user = dataManipulate.getUser(request.getLogin(), request.getRole());
            if (user != null) {
                if (user.getChat() != null) {
                    user.getChat().sendMessage(new CommandContainer(request.getLogin(), request.getRole(), request.getMsg()));
                    return json.toJson(new CommandContainer("Server", null, AnswerCode.GOOD_MGS));
                } else return json.toJson(new CommandContainer("Server", null, AnswerCode.DONT_HAVE_CLIENT));
            }
            return json.toJson(new CommandContainer("Server", null, AnswerCode.UNKNOWN_USER));
        }
        return json.toJson(new CommandContainer("Server", null, AnswerCode.UNKNOWN_FORM_REQUEST));
    }

    @POST
    @Path("/get")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getMsg_JSON(String msg) {
        PostMessageRequest request = json.fromJson(msg, PostMessageRequest.class);
        if (request.getRole() != null && request.getLogin() != null && request.getSizePage() != 0) {
            User user = dataManipulate.getUser(request.getLogin(), request.getRole());
            if (user != null) {
                if (user.getTypeApp() == TypeApp.REST) {
                    RestSocket restSocket = (RestSocket) user.getSocket();
                    return json.toJson(restSocket.getMessages(request.getNumberPage(), request.getSizePage()));
                }
                return json.toJson(new CommandContainer("Server", null, AnswerCode.UNKNOWN_MISTAKE));
            }
            return json.toJson(new CommandContainer("Server", null, AnswerCode.UNKNOWN_USER));
        }
        return json.toJson(new CommandContainer("Server", null, AnswerCode.UNKNOWN_FORM_REQUEST));
    }

}
