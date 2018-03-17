package hello.model;


import javax.persistence.*;
import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    private Integer type;

    @Column(unique=true, nullable=false)
    private String username;

    private String password;

    @Column(columnDefinition="Decimal(10,2) default '10.00'")
    private Double credit;

    private String token;

    /*public User(String username, String password, Integer type) throws NoSuchAlgorithmException {
        this.username = username;
        this.password = getMD5(password);
        this.type = type;
        this.credit = 10.00;
    }*/

    public User() { }

    public User(Integer id, String username,String password, Integer type, Double credit, String token){
        this.id = id;
        this.username = username;
        this.password = password;
        this.type = type;
        this.credit = credit;
        this.token = token;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = getMD5(password);
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = getMD5(this.id + token);
    }

    public static String getMD5(String input) {
        StringBuffer sb = new StringBuffer();

        try {
            MessageDigest md =
                    MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] mdbytes = md.digest();

            //convert the byte to hex format
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff)
                        + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        return sb.toString();
    }

}
