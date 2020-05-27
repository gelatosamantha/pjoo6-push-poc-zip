package com.solacesystems.jcsmp.samples.introsamples.intro;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import com.solacesystems.jcsmp.BytesMessage;
import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.SDTException;
import com.solacesystems.jcsmp.SDTMap;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.XMLContentMessage;
import com.solacesystems.jcsmp.XMLMessageListener;
import com.solacesystems.jcsmp.XMLMessageProducer;

public class TranslateConsumer implements XMLMessageListener {
	XMLMessageProducer prod;
	public TranslateConsumer(XMLMessageProducer prod) {
		// Constructor
		this.prod = prod; 
	}

	@Override
	public void onException(JCSMPException arg0) {
		// Print when error
		System.out.println(arg0);
		
	}

	@Override
	public void onReceive(BytesXMLMessage arg0) {
		BytesMessage outMsg = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
        String empty = "{}";		

        try {
          	SDTMap msgMap = arg0.getProperties();
           	outMsg.setProperties(msgMap);
           	if (msgMap != null) {
	    		String action = msgMap.get("action").toString();
	    		String topicName = msgMap.get("topicName").toString();
	    		// Send out compressed message
	    		if (action.equalsIgnoreCase("create")) {
	    			// Write create type message 
	    			ByteArrayOutputStream bos = new ByteArrayOutputStream(arg0.getBytes().length);  
	    			GZIPOutputStream gos = new GZIPOutputStream(bos);  
	    			gos.write(arg0.getBytes(), 0, arg0.getBytes().length);
	    			gos.finish();
	    			outMsg.setData(bos.toByteArray());
	    			System.out.println(bos.toByteArray().toString());
	    			System.out.println("Length ==> " + bos.toByteArray().length);
	    			prod.send(outMsg, JCSMPFactory.onlyInstance().createTopic(topicName));
	    			gos.flush();
	    			bos.flush();
	    		} else if (action.equalsIgnoreCase("update")) {
	    			// Write update type message 
	    			ByteArrayOutputStream bos = new ByteArrayOutputStream();  
	    			GZIPOutputStream gos = new GZIPOutputStream(bos);  
	    			gos.write(arg0.getBytes(), 0, arg0.getBytes().length);
	    			gos.finish();
	    			outMsg.setData(bos.toByteArray());
	    			prod.send(outMsg, JCSMPFactory.onlyInstance().createTopic(topicName));
	    			gos.flush();
	    			bos.flush();
	    		} else if (action.equalsIgnoreCase("delete")) {
	    			// Write delete type message
	    			ByteArrayOutputStream bos = new ByteArrayOutputStream();  
	    			GZIPOutputStream gos = new GZIPOutputStream(bos);  
	    			gos.write(empty.getBytes(), 0, empty.getBytes().length);
	    			gos.finish();
	    			outMsg.setData(bos.toByteArray());
	    			prod.send(outMsg, JCSMPFactory.onlyInstance().createTopic(topicName));
	    			gos.flush();
	    			bos.flush();
	    		}
           	}
    		arg0.ackMessage();
    			
    	} catch (SDTException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    		arg0.ackMessage();
    	} catch (JCSMPException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    		arg0.ackMessage();
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}            
  	}

}
