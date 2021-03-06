package com.guazi.ft.nr.controller;

import com.guazi.ft.cloud.RemoteService;
import com.guazi.ft.common.ExcelUtil;
import com.guazi.ft.common.JsonUtil;
import com.guazi.ft.dao.consign.model.UserDO;
import com.guazi.ft.nr.controller.model.ValidParent;
import com.guazi.ft.websocket.OrderWebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
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

	@Autowired
	private RemoteService remoteService;

	@GetMapping("/feign")
	public String feign() {
		UserDO user = new UserDO();
		user.setId(1L);
		user.setUsername("2");
		user.setPassword("3");
		user.setCreatedAt(new Date());
		user.setUpdatedAt(new Date(System.currentTimeMillis() * 10L));

		String json = remoteService.user(user);
		log.info("springCloud==>{}", json);
		return json;
	}

	@PutMapping("/valid-model")
	public String valid(@RequestBody @Valid ValidParent validParent) {
		return JsonUtil.object2Json(validParent);
	}

	@PostMapping("/socket/push")
	public String push(
			@RequestParam Integer oid,
			@RequestParam String msg
	) {
		OrderWebSocket.ORDER_WEB_SOCKET.entrySet().stream().filter(entry -> entry.getValue().equals(oid)).forEach(entry -> OrderWebSocket.sendMessage(entry.getKey(), msg));
		return "success";
	}

	@PostMapping("/upload")
	public String upload(
			@RequestParam String username,
			@RequestParam MultipartFile excel
	) {
		InputStream in = null;
		try {
			in = excel.getInputStream();
			return JsonUtil.object2Json(ExcelUtil.readExcel(in, false));
		} catch (Exception e) {
			return "error";
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
