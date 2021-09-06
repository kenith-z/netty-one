package xyz.hcworld.one.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: User
 * @Author: 张冠诚
 * @Date: 2021/9/3 11:43
 * @Version： 1.0
 */
@Data
public class User implements Serializable {

    public static final long serialVersionUID = 42L;

    private String username;

    private String password;

}
