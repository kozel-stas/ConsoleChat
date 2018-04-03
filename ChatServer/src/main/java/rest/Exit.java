package rest;

import com.google.gson.Gson;
import model.*;
import rest.SupporttClasses.PostMessageRequest;

import javax.jws.soap.SOAPBinding;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/exit")
public class Exit {
    private DataManipulate dataManipulate = DataManipulate.getInstance();
    private Gson json = new Gson();

    @Path("/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String exitClient_JSON(String msg) {
        PostMessageRequest request = json.fromJson(msg,PostMessageRequest.class);
        if (request.getLogin()!=null && request.getRole()!=null) {
            User user=dataManipulate.getUser(request.getLogin(),request.getRole());
            if (user!=null) {
                dataManipulate.remove(user);
                user.leave();
                return json.toJson(new CommandContainer("Server", null, AnswerCode.EXIT));
            }
            return json.toJson(new CommandContainer("Server", null, AnswerCode.UNKNOWN_USER));
        }
        return json.toJson(new CommandContainer("Server", null, AnswerCode.UNKNOWN_FORM_REQUEST));
    }


}
