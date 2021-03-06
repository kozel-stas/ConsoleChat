package rest;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import model.*;
import model.SupportClasses.AnswerCode;
import model.SupportClasses.CommandContainer;
import model.User;
import rest.SupporttClasses.PostMessageRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/exit")
@Api(value = "Exit")
public class Exit {
    private DataManipulate dataManipulate = DataManipulate.getInstance();
    private Gson json = new Gson();


    @Path("/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "exit1515611616")
    @ApiImplicitParams({@ApiImplicitParam(
        name = "Exitjktfytghjkljhgcf",
        dataType ="string"
    )})
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
