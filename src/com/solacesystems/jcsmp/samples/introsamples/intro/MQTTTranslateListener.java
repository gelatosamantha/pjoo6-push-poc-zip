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

public class MQTTTranslateListener implements XMLMessageListener {
	XMLMessageProducer prod;
	String topicName;
	public MQTTTranslateListener(XMLMessageProducer prod, String topicName) {
		// Constructor
		this.prod = prod; 
		this.topicName = topicName;
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
        	// Send out message
   			ByteArrayOutputStream bos = new ByteArrayOutputStream(arg0.getBytes().length);  
    		GZIPOutputStream gos = new GZIPOutputStream(bos);  
    		gos.write(arg0.getBytes(), 0, arg0.getBytes().length);
    		gos.finish();
    		// Send compressed data
    		outMsg.setData(bos.toByteArray());
    		prod.send(outMsg, JCSMPFactory.onlyInstance().createTopic(topicName));
    		gos.flush();
    		bos.flush();
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
