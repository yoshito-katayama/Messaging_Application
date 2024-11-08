package com.example.demo.controllers;

import com.example.demo.services.MessageService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.jdbc.TestTable;

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
	@GetMapping("check")
    public @ResponseBody ResponseEntity<String> checkMessage() {
        List<String> list = new ArrayList<>();

        jdbcTemplate.query(
            "select * from test_table",
            (rs, rowNum) -> rs.getString("MESSAGE")
        ).forEach(thing -> list.add(thing.toString()));

        return ResponseEntity.ok("{ \"message\" : \"Message Contents: "  + list +"\" }");
    }

	/** Db2にインサートする関数 **/
	@PostMapping("insert")
	public @ResponseBody ResponseEntity<String> addMessage(@RequestParam("msg") String msg) {
		try {
			String sql = "INSERT INTO test_table (message) VALUES (?)";
			jdbcTemplate.update(sql, msg);

			return ResponseEntity.ok("{ \"message\" : \"Message Insert: "  + msg +"\" }");
		} catch (Exception e) {
			return ResponseEntity.ok("{ \"message\" : \"Some errors occured on sending the message: "+ msg + "\" }");
		}
	}

	/** メッセージ(msg)をMQサーバに送信する関数 **/
	@GetMapping("send")
	public String send(@RequestParam("msg") String msg) {
		/** MessageService.javaを呼び出し、MQに送信 **/
		return messageService.send(msg);
	}

	/** メッセージをMQサーバから受信する関数 **/
	@GetMapping("recv")
	public String recv() {
		/** MessageService.javaを呼び出し、MQから受信 **/
		return messageService.recv();
	}

	/** MQのアドレスを取得する関数 **/
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
