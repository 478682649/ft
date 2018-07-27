package com.guazi.ft.nr.controller;

import com.guazi.ft.dao.consign.model.UserDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * NrController
 *
 * @author shichunyang
 */
@RestController
@CrossOrigin
@Slf4j
public class NrController {

    //@Autowired
    //private RemoteService remoteService;

    @GetMapping("/feign")
    public String feign() {
        UserDO user = new UserDO();
        user.setId(1L);
        user.setUsername("2");
        user.setPassword("3");
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date(System.currentTimeMillis() * 10L));

        //String json = remoteService.user(user);
        //log.info("springCloud==>{}", json);
        //return json;
        return "success";
    }
}
