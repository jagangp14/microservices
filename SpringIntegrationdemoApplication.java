package com.hsbc.banking.apps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;

import com.hsbc.banking.interfaces.PrinterGateway;
import com.hsbc.banking.service.PrintService;

@SpringBootApplication
@Configuration
@ComponentScan(basePackages="com.hsbc.*")
@ImportResource("integration-context.xml")
public class SpringIntegrationdemoApplication implements ApplicationRunner{
    private Message<String> message,greetMessage;
    @Autowired
    private PrintService printService;
    @Autowired
    @Qualifier("inputChannel")
    private DirectChannel inputChannel;
    @Autowired
    @Qualifier("outputChannel")
    private DirectChannel outputChannel;
    @Autowired
    PrinterGateway printerGateway;
	public static void main(String[] args) {
		SpringApplication.run(SpringIntegrationdemoApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("Welcome to Spring Integration....");
		//Message Header
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("1234", "jsessionId");
		MessageHeaders header=new MessageHeaders(map);
		//Message
	    message=new GenericMessage<String>("Spring Integration...",header);
	    printService.print(message);
	    
	    
	    
	    
	    
	    
	    //channel
	    outputChannel.subscribe(new MessageHandler()
	    		{

					@Override
					public void handleMessage(Message<?> message) throws MessagingException {
						// TODO Auto-generated method stub
						System.out.println(message.getPayload());
					}
	    	
	    		});
	    
	    //alternative way to build message
	    greetMessage=MessageBuilder.withPayload("Good Move...").setHeader("2345", "greet").build();
	    inputChannel.send(greetMessage);
	    
	    //Message Template
	    
	   // MessagingTemplate messagingTemplate=new MessagingTemplate();
	   // messagingTemplate.sendAndReceive(inputChannel, greetMessage);
	   // Message<?> responseMessage=messagingTemplate.receive();
	    //System.out.println(responseMessage.getPayload());
	    
	    //Gateway messaging
	    
	    List<Future<Message<String>>> futures=new ArrayList<Future<Message<String>>>();
	    Message<String> futureMessage=null;
	    for(int i=0;i<10;i++)
	    {
	    	futureMessage=MessageBuilder.withPayload("Future message occurring"+i).setHeader("Header",i).build();
	    	futures.add(printerGateway.print(futureMessage));
	    }
	    
	    for(Future<Message<String>> future : futures)
	    {
	    	System.out.println(future.get().getPayload());
	    }
	    
	}
}
