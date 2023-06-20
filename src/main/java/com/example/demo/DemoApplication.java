package com.example.demo;

import com.ciscospark.Message;
import com.ciscospark.Room;
import com.ciscospark.Spark;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.json.Json;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
@RestController

public class DemoApplication {

	String accessToken = "MDQ3ZGNkYmEtOWVmZi00MjZhLWIyZDgtM2I4NTIwZDU0MWI5YmY3NjUxODUtNjlk_P0A1_35ce7cdd-39eb-4c36-a942-57c88cee9f29";

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	public @PostMapping("/webhook") String receive(JSONObject requestBody){

		Spark spark = Spark.builder()
				.baseUrl(URI.create("https://webexapis.com/v1"))
				.accessToken(accessToken)
				.build();
		//print info to the console
		System.out.println("Request received");
		System.out.println(requestBody.toString());

		String resource = (String) requestBody.get("resource");
		JSONObject data = (JSONObject) requestBody.get("data");
		String roomId = (String) data.get("roomId");

		if (resource.equals("messages")) {
			String messageId = (String) data.get("id");
			Message message = spark.messages().path("/" + messageId).get();

			if (!message.getPersonEmail().contains("@webex.bot")) {
				//split the message along spaces
				String[] trimmedMessage = message.getText().split("\\s");
				//convert the message array into a searchable list
				ArrayList<String> messageList = new ArrayList<>(Arrays.asList(trimmedMessage));
				messageList.stream().forEach(System.out::println);
			}
		}
		return "Received the message";
	}

	public @GetMapping("/") String sayHello() {
		// Initialize the client
		Spark spark = Spark.builder()
				.baseUrl(URI.create("https://webexapis.com/v1"))
				.accessToken(accessToken)
				.build();
		// Create a new room
		Room room = new Room();
		room.setTitle("Hello World");
		room = spark.rooms().post(room);
		System.out.println("Webex Room Created on::"+room.getCreated());
		Message message = new Message();
		message.setRoomId(room.getId());
		message.setText("Hello World!");
		spark.messages().post(message);
		return spark.memberships().get().getPersonEmail();
	}


}
