package pers.ccdongyu.neo4jbackend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pers.ccdongyu.neo4jbackend.domain.Dynamic;
import pers.ccdongyu.neo4jbackend.domain.Person;
import pers.ccdongyu.neo4jbackend.message.Status;
import pers.ccdongyu.neo4jbackend.message.StatusWithTime;
import pers.ccdongyu.neo4jbackend.repository.DynamicRepository;
import pers.ccdongyu.neo4jbackend.repository.PersonRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DynamicService {

    private final DynamicRepository dynamicRepository;
    private final PersonRepository personRepository;

    public DynamicService(DynamicRepository dynamicRepository, PersonRepository personRepository){
        this.dynamicRepository = dynamicRepository;
        this.personRepository = personRepository;
    }

    public StatusWithTime releaseDynamic(String userid, String content, List<String> contents_img){
        Person person = personRepository.findByUserid(userid);
        Logger log = LoggerFactory.getLogger(this.getClass());
        log.info(userid);
        if(person == null){
            return StatusWithTime.getFailedInstance("无该用户");
        }
        Dynamic dynamic = new Dynamic(content, userid, contents_img);
        dynamicRepository.save(dynamic);
        dynamicRepository.createDynamic(userid, dynamic.getId(), Calendar.getInstance().getTime());
        return StatusWithTime.getInstanceWithTime(200, "release success", null);
    }


    public StatusWithTime getDynamicList(String userid) {
        class DynamicListItem{
            public String userid;
            public String avatar;
            public String username;
            public String contents;
            public List<String> contents_img;
            public String create_time;
        }

        Person person = personRepository.findByUserid(userid);
        if(person == null){
            return StatusWithTime.getFailedInstance("无该用户");
        }

        Map<String, List<DynamicListItem>> data = new HashMap<>();
        List<Dynamic> dynamics = dynamicRepository.getDynamicsByUserid(userid);
        List<DynamicListItem> listItems = new LinkedList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Dynamic d : dynamics) {
            DynamicListItem listItem = new DynamicListItem();
            listItem.contents = d.getContents();
            listItem.userid = d.getUserid();
            listItem.create_time = sdf.format(new Date(Long.parseLong(String.valueOf(dynamicRepository.getCreateTime(d.getId())))));
            listItem.avatar = person.getAvatar();
            listItem.username = person.getUsername();
            listItem.contents_img = d.getContents_img() == null? new LinkedList<>():d.getContents_img();
            listItems.add(listItem);
        }

        Collections.sort(listItems, new Comparator<DynamicListItem>() {
            @Override
            public int compare(DynamicListItem o1, DynamicListItem o2) {
                Date o1Date = null;
                Date o2Date = null;
                try {
                    o1Date = sdf.parse(o1.create_time);
                    o2Date = sdf.parse(o2.create_time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return o2Date.compareTo(o1Date);
            }
        });

        data.put("dynamic_lists", listItems);

        return StatusWithTime.getInstanceWithTime(200, "get dynamic list success", data);
    }

    public Status deleteDynamic(Long nodeId) {
        if (dynamicRepository.findDynamicById(nodeId) == null){
            return Status.getFailedInstance("无效的动态编号");
        }
        dynamicRepository.deleteDynamicById(nodeId);
        return Status.getSucceedInstance();
    }
}
