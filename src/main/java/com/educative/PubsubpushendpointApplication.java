package com.educative;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@SpringBootApplication
@RestController
public class PubsubpushendpointApplication {
	
	@Autowired Gson gson;
	@Autowired HttpServletRequest httpServletRequest;
	
	private Message getMessage() {
		Message message = null;
		try {
			String requestBody = httpServletRequest.getReader().lines().collect(Collectors.joining("\n"));
			JsonElement jsonRoot = JsonParser.parseString(requestBody);
			String messageStr = jsonRoot.getAsJsonObject().get("message").toString();
			message = gson.fromJson(messageStr, Message.class);
			String decoded = new String(Base64.getDecoder().decode(message.getData()));
			message.setData(decoded);
		} catch (IOException ioex) {
			System.out.println("IOException occurred while parsing message from Http Servlet Request: "+ ioex);
		} catch (Exception e) {
			System.out.println("Exception occurred while parsing message from Http Servlet Request: " + e);
		}
		return message;
	}
	
	List<Message> messageList = new ArrayList<>();
	
	@RequestMapping(value="/messages", method=RequestMethod.POST)
	public ResponseEntity<Object> messagesEndpoint() {
		Message message = getMessage();
		messageList.add(message);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/messages", method=RequestMethod.GET)
	public ResponseEntity<Object> messages() {
		return new ResponseEntity<>(messageList, HttpStatus.OK);
	}

	public static void main(String[] args) {
		SpringApplication.run(PubsubpushendpointApplication.class, args);
	}

}
