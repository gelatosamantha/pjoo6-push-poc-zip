/**
 *  Copyright 2012-2020 Solace Corporation. All rights reserved.
 *
 *  http://www.solace.com
 *
 *  This source is distributed under the terms and conditions
 *  of any contract or contracts between Solace and you or
 *  your company. If there are no contracts in place use of
 *  this source is not authorized. No support is provided and
 *  no distribution, sharing with others or re-use of this
 *  source is authorized unless specifically stated in the
 *  contracts referred to above.
 *
 *  HelloWorldPub
 *
 *  This sample shows the basics of creating session, connecting a session,
 *  and publishing a direct message to a topic. This is meant to be a very
 *  basic example for demonstration purposes.
 */

package com.solacesystems.jcsmp.samples.introsamples.intro;
import java.io.*;
import java.util.Scanner;

import com.solacesystems.jcsmp.BytesMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.JCSMPStreamingPublishEventHandler;
import com.solacesystems.jcsmp.Queue;
import com.solacesystems.jcsmp.SDTMap;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.XMLMessageProducer;

public class SimplePublisher {
    
    public static void main(String... args) throws JCSMPException {
        String currentDirectory = System.getProperty("user.dir");
        System.out.println("The current working directory is " + currentDirectory);
    	String hostString = "";
    	String userName = "";
    	String passwordString = "";
    	String vpnName = "";
    	String queueName = "";

    	// File file = new File(currentDirectory+File.separator+"bin"+File.separator+"com"+File.separator+"solacesystems"+File.separator+"jcsmp"+File.separator+"samples"+File.separator+"introsamples"+File.separator+"intro"+File.separator+"config.txt");
    	File file = new File(currentDirectory+File.separator+"config.txt");

    	BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
	    	String st;
	    	Integer arg = 0;
	    	Integer total_param = 0;
	    	while ((st = br.readLine()) != null) { 
	    		switch (arg) {
	    			case(1):
	    				// Read host
	    				hostString = st;
	    				total_param++;
	    				arg = 0;
	    			case(2):
	    				// Read username
	    				userName = st;
	    				total_param++;
	    				arg = 0;
	    			case(3):
	    				// Read passowrd
	    				passwordString=st;
	    				total_param++;
	    				arg = 0;
	    			case(4):
	    				// Read vpn
	    				vpnName=st;
	    				total_param++;
	    				arg = 0;
	    			case(5):
	    				// Read queue
	    				queueName=st;
	    				total_param++;
	    				arg = 0;
	    			default:
	    	    		if (st.equalsIgnoreCase("HostString1")) {
	    	    			arg = 1;
	    	    		}
	    	    	    if (st.equalsIgnoreCase("Username1")) {
	    	    	    	arg = 2;
	    	    	    }
	    	    	    if (st.equalsIgnoreCase("Password1")) {
	    	    	    	arg = 3;
	    	    	    }
	    	    	    if (st.equalsIgnoreCase("VPN1")) {
	    	    	    	arg = 4;
	    	    	    }
	    	    	    if (st.equalsIgnoreCase("Queue1")) {
	    	    	    	arg = 5;
	    	    	    }
	    		}

	    	  } 
	    	if (total_param < 5) {
	            System.out.println("Not enough information in config file");
	            System.exit(-1);
	    	}

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        System.out.println("SimplePublisher initializing...");

    	// Create a JCSMP Session
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, hostString);      		// msg-backbone ip:port
        properties.setProperty(JCSMPProperties.VPN_NAME, vpnName);  		// message-vpn
        properties.setProperty(JCSMPProperties.USERNAME, userName);  		// client-username
        properties.setProperty(JCSMPProperties.PASSWORD, passwordString);	// password
        final JCSMPSession session =  JCSMPFactory.onlyInstance().createSession(properties);
        
        final Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);
        
        session.connect();
        /** Anonymous inner-class for handling publishing events */
        XMLMessageProducer prod = session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
            public void responseReceived(String messageID) {
                System.out.println("Producer received response for msg: " + messageID);
            }
            public void handleError(String messageID, JCSMPException e, long timestamp) {
                System.out.printf("Producer received error for msg: %s@%s - %s%n",
                        messageID,timestamp,e);
            }
        });

        // Publish-only session is now hooked up and running!
        
        BytesMessage msg = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
        TextMessage txt_msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        SDTMap userData = JCSMPFactory.onlyInstance().createMap();
        userData.putString("topicName", "test");
        userData.putString("action", "create");
        msg.setProperties(userData);
        
        		
        String text = "{'messge':'Hello world!'}";
        
        Scanner keyboard = new Scanner(System.in);
        String key = "nn";
        while(key.compareToIgnoreCase("e")!=0) {
        	String topicName = "test";
        	System.out.println("Choose a action : (c)reate message, (u)pdate topic, (d)elete, (e)xit");
        	key = keyboard.nextLine();
			if (key.equalsIgnoreCase("c")) {
				System.out.println("Choose a topic to create :");
				topicName = keyboard.nextLine();
	        	System.out.println("Your payload");
	        	text = keyboard.nextLine();
	        	msg.setData(text.getBytes());
				// Send create topic message
			    userData.remove("action");
				userData.remove("topicName");
				userData.putString("topicName", topicName);
				userData.putString("action", "create");
				msg.setProperties(userData);

				txt_msg.setText(text);
				txt_msg.setProperties(userData);
				// Send to Solace
				prod.send(msg, queue);
			} else if (key.equalsIgnoreCase("u")) {
				System.out.println("Choose a topic to update :");
				topicName = keyboard.nextLine();
	        	System.out.println("Your payload");
	        	text = keyboard.nextLine();
	        	msg.setData(text.getBytes());
				// Send update topic message
			    userData.remove("action");
				userData.remove("topicName");
				userData.putString("topicName", topicName);
				userData.putString("action", "update");
				msg.setProperties(userData);
					
				// Send to Solace                		
				prod.send(msg, queue);
			}else if (key.equalsIgnoreCase("d")) {
				// Send delete topic message
				System.out.println("Choose a topic to delete :");
				topicName = keyboard.nextLine();
	        	System.out.println("Your payload");
	        	text = keyboard.nextLine();
	        	msg.setData(text.getBytes());
			    userData.remove("action");
				userData.remove("topicName");
				userData.putString("topicName", topicName);
				userData.putString("action", "delete");
				msg.setProperties(userData);

				// Send to Solace
				prod.send(msg, queue);
			}else {
					// Do nothing
			}        	
        }
        
        System.out.println("Message sent. Exiting.");
        session.closeSession();
    }
}
