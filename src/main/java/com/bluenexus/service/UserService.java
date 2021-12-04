package com.bluenexus.service;

import com.bluenexus.dto.UserDto;
import com.bluenexus.model.User;
import com.bluenexus.model.Wallet;

public interface UserService {

    User save(UserDto userDto);

    Wallet viewWallet(int userId);

    User login(UserDto userDto);
}
