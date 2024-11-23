package com.example.demo.controllers;

import com.example.demo.services.MessageService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

	private final MessageService messageService;
	@Value("${ibm.mq.connName}")
    private String connName;

	@Autowired
	/** Db2 **/
	private JdbcTemplate jdbcTemplate;
	/** MQ **/
	public DemoController(MessageService messageService) {
		this.messageService = messageService;
	}

	/** Db2の内容を照会する関数 **/
	@CrossOrigin
	@GetMapping("check")
	/** @CrossOrigin **/
    public @ResponseBody ResponseEntity<String> checkMessage() {
        List<String> list = new ArrayList<>();

        jdbcTemplate.query(
            "select * from test_table",
            (rs, rowNum) -> rs.getString("MESSAGE")
        ).forEach(thing -> list.add(thing.toString()));

        return ResponseEntity.ok("{ \"message\" : \"Message Contents: "  + list +"\" }");
    }

	/** メッセージ(msg)をMQサーバに送信する関数 **/
	@CrossOrigin
	@GetMapping("send")
	public String send(@RequestParam("msg") String msg) {
		/** MessageService.javaを呼び出し、MQに送信 **/
		return messageService.send(msg);
	}

	/** MQのアドレスを取得する関数 **/
	@CrossOrigin
	@GetMapping("address")
	public String address() {
		try {
			String address_ = connName.split("\\(")[0];;			
			return "{ \"message\" : \""+ address_ +"\",  \"message\" : \""+ address_ +"\" }";
		} catch(Exception e) {
			return "{ \"message\" : \"Please ensure that your application.properties file is set up correctly.\" }";
		}	
	}
}