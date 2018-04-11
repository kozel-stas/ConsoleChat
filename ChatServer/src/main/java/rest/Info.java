package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.swagger.annotations.Api;
import model.*;
import model.SupportClasses.ConvertUser;
import model.SupportClasses.Role;
import org.apache.commons.collections4.CollectionUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Api(value = "Info")
@Path("/info")
public class Info {
    private static FindAgentSystem findAgentSystem = FindAgentSystem.getInstance();
    private static DataManipulate dataManipulate = DataManipulate.getInstance();
    private static Gson json = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    private static Gson gsonUser = new GsonBuilder().registerTypeAdapter(User.class,new ConvertUser()).create();


    @Path("/getAllRegisterAgent")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String AllRegisterAgent_JSON() {
        return json.toJson(dataManipulate.getRegisterAgent());
    }

    @Path("/getAllRegisterClient")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String AllRegisterClient_JSON() {
        return json.toJson(dataManipulate.getRegisterClient());
    }

    @Path("/getFreeAgents")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String FreeAgents_JSON() {
        return json.toJson(findAgentSystem.getFreeAgents());
    }


    @Path("/getWaitClients")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String WaitClients_JSON() {
        return json.toJson(findAgentSystem.getWaitClients());
    }


    @Path("/getChats")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String chats_JSON() {
        return json.toJson(Chat.getChats());
    }

    @Path("/getInfoAgent")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String infoAgent_JSON(@QueryParam("login") String login) {
        if(login==null) return gsonUser.toJson(null);
        User user = dataManipulate.getUser(login,Role.AGENT);
        if(user!=null) return gsonUser.toJson( dataManipulate.getUser(login,Role.AGENT));
        if(dataManipulate.find(login,Role.AGENT)) return gsonUser.toJson(new User(login,null,Role.AGENT,null));
        return gsonUser.toJson( null);
    }

    @Path("/getInfoClient")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String infoClient_JSON(@QueryParam("login") String login) {
        if(login==null) return gsonUser.toJson(null);
        User user = dataManipulate.getUser(login,Role.CLIENT);
        if(user!=null) return gsonUser.toJson( dataManipulate.getUser(login,Role.CLIENT));
        if(dataManipulate.find(login,Role.CLIENT)) return gsonUser.toJson(new User(login,null,Role.CLIENT,null));
        return gsonUser.toJson(null);
    }

    @Path("/getInfoChat")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String infoChat_JSON(@QueryParam("ID") long ID) {
        return json.toJson(Chat.getInfoChat(ID));
    }

}
