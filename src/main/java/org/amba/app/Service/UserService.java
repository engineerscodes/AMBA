package org.amba.app.Service;


import org.amba.app.Entity.User;
import org.amba.app.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepo userRepo;


    public User saveUser(User user){
        return userRepo.saveAndFlush(user);
    }

}
