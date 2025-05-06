package sk.tuke.gamestudio.kakuro.entity.DTO;

public class PlayerRegistrationDTO {
    private final String nickname;
    private final String password;
    private final String avatar;

    public PlayerRegistrationDTO(String nickname, String password, String avatar) {
        this.nickname = nickname;
        this.password = password;
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }
    public String getPassword() {
        return password;
    }
    public String getAvatar() {
        return avatar;
    }
}
