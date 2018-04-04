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
import java.util.regex.Pattern;

@Path("/login")
public class Login {
    private static Pattern validateLogin = Pattern.compile("^[A-z0-9]*$");
    private static DataManipulate dataManipulate=DataManipulate.getInstance();
    private static Gson json=new Gson();

    @Path("/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String LoginClient_JSON(String msg){
        PostMessageRequest request = json.fromJson(msg,PostMessageRequest.class);
        if (request.getRole() != null && request.getLogin() != null) {
            if (validateLogin.matcher(request.getLogin()).find()) {
                User user = new User(request.getLogin(), new RestSocket(), request.getRole(), TypeApp.REST);
                CommandContainer commandContainer = dataManipulate.login(user);
                return json.toJson(commandContainer);
            }
            return json.toJson(new CommandContainer("Server",null,AnswerCode.INVALID_CHARACTERS));
        }
        return json.toJson(new CommandContainer("Server", null, AnswerCode.UNKNOWN_FORM_REQUEST));
    }

}
