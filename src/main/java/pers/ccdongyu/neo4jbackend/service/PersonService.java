package pers.ccdongyu.neo4jbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pers.ccdongyu.neo4jbackend.domain.Person;
import pers.ccdongyu.neo4jbackend.message.Status;
import pers.ccdongyu.neo4jbackend.repository.PersonRepository;

import java.util.Random;

@Service
public class PersonService {
    private final String[] avatar = new String[]{
            "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2802691956,955693789&fm=27&gp=0.jpg",
            "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3846895839,2711067435&fm=27&gp=0.jpg",
            "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1762425966,1375965910&fm=27&gp=0.jpg",
            "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1616787564,2143232655&fm=27&gp=0.jpg",
            "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=650906246,1112337702&fm=27&gp=0.jpg",
            "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=659071980,1632459771&fm=27&gp=0.jpg"
    };

    private final PersonRepository personRepository;


    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Status login(String userid, String password){
        Person p;
        Logger log = LoggerFactory.getLogger(this.getClass());
        log.info(userid);
        if((p = personRepository.findByUserid(userid))==null){
            return Status.getFailedInstance("用户未注册！");
        }else if(password !=null && password.equals(p.getPassword())){
            return Status.getInstance(200, "", p.clearPassword());
        }else{
            return Status.getFailedInstance("密码错误！");
        }

    }

    public Status register(Person person){
        String id;
        if ((id=person.getUserid()) ==null){
            return Status.getFailedInstance("用户名不能为空！");
        }
        if(personRepository.findByUserid(id) != null){
            return Status.getFailedInstance("用户已注册");
        }
        person.setAvatar(avatar[new Random().nextInt(avatar.length)]);
        personRepository.save(person.assignName());
        return Status.getSucceedInstance();
    }

    public Status becomFriend(String userid, String friendid){
        personRepository.becomeFriend(userid,friendid);
        return Status.getSucceedInstance();
    }

    public Status detachFriend(String userid, String friendid){
        personRepository.detachFriend(userid, friendid);
        return Status.getSucceedInstance();
    }

    public Status friendList(String userid){
        return Status.getInstance(200,"",personRepository.getAllFriends(userid).stream().map(Person::clearPassword));
    }

    public Status friendsMayKnow(String friendid){
        return Status.getInstance(200,"",personRepository.getAllFriends(friendid).stream().map(Person::clearPassword));
    }

    public Status setMessage(String userid,String username,String sex,String desc){
        if(personRepository.findByUserid(userid)==null) {
            return Status.getFailedInstance("用户未注册！");
        }
        personRepository.changeMessage(userid,username,sex,desc);
        return Status.getSucceedInstance();
    }
}
