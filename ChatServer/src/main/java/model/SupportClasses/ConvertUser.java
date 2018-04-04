package model.SupportClasses;

import com.google.gson.*;
import model.Chat;
import model.User;

import java.lang.reflect.Type;
import java.util.List;

public class ConvertUser implements JsonSerializer<User> {

    @Override
    public JsonElement serialize(User user, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("login", user.getLogin());
        jsonObject.addProperty("role",user.getRole().toString());
        jsonObject.addProperty("typeApp",user.getTypeApp().toString());
        JsonArray jsonArray =new JsonArray();
        for (Chat chat: (List<Chat>)user.getChats()) {
           JsonObject object =new JsonObject();
           object.addProperty("ID",chat.getID());
           jsonArray.add(object);
        }
        jsonObject.add("chat",jsonArray);
        return jsonObject;
    }
}
