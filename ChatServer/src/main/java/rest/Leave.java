package rest;

import com.google.gson.Gson;
import model.*;
import model.SupportClasses.AnswerCode;
import model.SupportClasses.CommandContainer;
import model.SupportClasses.Role;
import model.User;
import rest.SupporttClasses.PostMessageRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/leave")
public class Leave {
    private static DataManipulate dataManipulate=DataManipulate.getInstance();
    private static Gson json=new Gson();

    @Path("/")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String leave_JSON(String msg){
        PostMessageRequest request = json.fromJson(msg, PostMessageRequest.class);
        if (request.getRole() != null && request.getLogin() != null) {
            if(request.getRole()==Role.CLIENT) {
                User user = dataManipulate.getUser(request.getLogin(), request.getRole());
                if (user != null) {
                    user.leave();
                    return json.toJson(new CommandContainer("Server",null,AnswerCode.GOOD_LEAVE));
                }
                return json.toJson(new CommandContainer("Server",null,AnswerCode.UNKNOWN_USER));
            }
            return json.toJson(new CommandContainer("Server",null,AnswerCode.CAN_NOT_LEAVE_AGENT));
        }
        return json.toJson(new CommandContainer("Server", null, AnswerCode.UNKNOWN_FORM_REQUEST));
    }
}
