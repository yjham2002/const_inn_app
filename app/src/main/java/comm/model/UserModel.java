package comm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import bases.utils.PreferenceUtil;

public class UserModel extends PrefModel{

    private int userNo;
    private boolean isAutoLogin;
    private String account;
    private String password;
    private String name;
    private String nickName;
    private String registrationDate;
    private String messageToken;
    private int age;
    private String phoneNumber;

    public UserModel(){}

    @Override
    @JsonIgnore
    public String toJson() throws IOException{
        final ObjectMapper objectMapper = new ObjectMapper();
        final String json = objectMapper.writeValueAsString(this);
        return json;
    }

    @JsonIgnore
    public static UserModel parseJson(String json) throws IOException{
        final ObjectMapper objectMapper = new ObjectMapper();
        final UserModel userModel = objectMapper.readValue(json, UserModel.class);
        return userModel;
    }

    public static UserModel getFromPreference(){
        final ObjectMapper objectMapper = new ObjectMapper();
        UserModel userModel = null;
        try {
            final String json = PreferenceUtil.getString(getPrefId());
            if(json != null && !json.equals("")){
                userModel = objectMapper.readValue(json, UserModel.class);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            return userModel;
        }
    }

    public boolean saveAsPreference(){
        try {
            final String json = toJson();
            PreferenceUtil.setString(getPrefId(), json);
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean isAutoLogin() {
        return isAutoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        isAutoLogin = autoLogin;
    }

    private static String getPrefId(){
        return String.format("kr.co.picklecode.Pref.UserModel");
    }

    public int getUserNo() {
        return userNo;
    }

    public void setUserNo(int userNo) {
        this.userNo = userNo;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getMessageToken() {
        return messageToken;
    }

    public void setMessageToken(String messageToken) {
        this.messageToken = messageToken;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userNo=" + userNo +
                ", isAutoLogin=" + isAutoLogin +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", messageToken='" + messageToken + '\'' +
                ", age=" + age +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
