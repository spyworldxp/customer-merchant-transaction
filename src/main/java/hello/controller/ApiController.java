package hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import hello.model.User;
import hello.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping(path="/api")
public class ApiController {
    @Autowired

    private UserRepository userRepository;
    private Environment env;

    @Value("${application.newcredit}")
    private Double credit;

    @Value("${application.transfer.usercap}")
    private Double userCap;

    @Value("${application.transfer.b2b}")
    private Boolean B2BTransfer;

    @RequestMapping(path="/add") // Map ONLY GET Requests
    public @ResponseBody Object addNewUser (@RequestParam String username, @RequestParam String password, @RequestParam(value="type", defaultValue="1") Integer type) {
        if(userRepository.findUserByUsername(username) == null){
            User u = new User();
            u.setUsername(username);
            u.setPassword(password);
            u.setType(type);
            u.setCredit(credit);
            try{
                u = userRepository.save(u);
            } catch(Exception e){
                return new ResponseEntity<Object>("unable to create account", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<Object>(u, HttpStatus.OK);
        }else{
            return new ResponseEntity<Object>("username exist", HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/login")
    public ResponseEntity login(@RequestParam String username, @RequestParam String password) {
        User loginUser = userRepository.findUserByUsernameAndPassword(username,User.getMD5(password));
        if(loginUser == null){
            return new ResponseEntity<String>("wrong username or password", HttpStatus.NOT_FOUND);
        }else{
            String time = LocalDateTime.now().atZone(ZoneOffset.UTC).toString();
            loginUser.setToken(time);
            userRepository.save(loginUser);

            return new ResponseEntity<String>(loginUser.getToken(), HttpStatus.OK);
        }
    }

    @RequestMapping("/transfer")
    public ResponseEntity transfer(@RequestParam String fromUser, @RequestParam String token, @RequestParam String toUser, @RequestParam Double amount) {
        User currentUser = userRepository.findUserByUsernameAndToken(fromUser,token);
        User transferUser = userRepository.findUserByUsername(toUser);

        if(currentUser == null){
            return new ResponseEntity<String>("Invalid token", HttpStatus.NOT_FOUND);
        }

        if(currentUser.getId() == transferUser.getId()){
            return new ResponseEntity<String>("Can't transfer back to same account", HttpStatus.NOT_FOUND);
        }

        if(BigDecimal.valueOf(amount).scale() > 2){
            return new ResponseEntity<String>("Please enter the right amount", HttpStatus.NOT_FOUND);
        }

        if(transferUser == null){
            return new ResponseEntity<String>("Unable to transfer credit to "+toUser, HttpStatus.NOT_FOUND);
        }

        Double userCredit = currentUser.getCredit();

        if(userCredit < amount){
            return new ResponseEntity<String>("Insufficient credit", HttpStatus.NOT_FOUND);
        }

        if(B2BTransfer == false && currentUser.getType() == 2 && currentUser.getType() == transferUser.getType()){
            return new ResponseEntity<String>("Not allow to transfer credit to merchant", HttpStatus.NOT_FOUND);
        }

        /*if(userCap < amount && currentUser.getType() == 1){
            return new ResponseEntity<String>("Not allow to transfer more than "+userCap, HttpStatus.NOT_FOUND);
        }*/

        if(userCap < amount){
            return new ResponseEntity<String>("Not allow to transfer more than "+userCap, HttpStatus.NOT_FOUND);
        }

        currentUser.setCredit(currentUser.getCredit() - amount);
        transferUser.setCredit(transferUser.getCredit() + amount);

        userRepository.save(currentUser);
        userRepository.save(transferUser);

        return new ResponseEntity<String>("Okay", HttpStatus.OK);
    }

}