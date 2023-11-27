package lib;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class DataGeneretor {
    public static String generateNewEmail(){
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        return "learnQa" + timeStamp + "@example.com";
    }
    public static Map<String,String> getRegistrationData(){
        Map<String,String> authData = new HashMap<>();
        authData.put("email",generateNewEmail());
        authData.put("password","123");
        authData.put("username","user");
        authData.put("firstName","fNam");
        authData.put("lastName","lNam");
        return authData;
    }
    public static  Map<String,String> getRegistrationData(Map<String,String> nonDefaultValues){
        Map<String,String> defaultValues = DataGeneretor.getRegistrationData();
        Map<String,String> userData = new HashMap<>();

        String[] keys = {"username", "firstName", "lastName", "email"};
        for (String key : keys){
            if(nonDefaultValues.containsKey(key)){
                userData.put(key,nonDefaultValues.get(key));
            } else {
                userData.put(key,defaultValues.get(key));
            }
        }
        return userData;
    }
}
