package hello.repository;
import org.springframework.data.repository.CrudRepository;

import hello.model.User;

import java.util.List;


public interface UserRepository extends CrudRepository<User, Long> {

    User findUserByUsername(String username);
    User findUserByUsernameAndPassword(String username, String password);
    User findUserByUsernameAndToken(String username, String token);

}