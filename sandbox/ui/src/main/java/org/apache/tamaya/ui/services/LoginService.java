package org.apache.tamaya.ui.services;

import org.apache.tamaya.ui.User;

/**
 * Created by atsticks on 29.03.16.
 */
public interface LoginService {

    User login(String userId, String credentials);

}
