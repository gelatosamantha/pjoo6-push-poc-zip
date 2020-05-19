package com.solacesystems.jcsmp.samples.introsamples.intro;
import com.solacesystems.jcsmp.BytesMessage;
import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.SDTException;
import com.solacesystems.jcsmp.SDTMap;
import com.solacesystems.jcsmp.TextMessage;
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
        if (arg0 instanceof BytesMessage) {
            System.out.printf("BytesMessage received: '%s'%n",
                    ((BytesMessage)arg0).dump());
        } else {
            System.out.println("Message received.");
        }
        BytesMessage outMsg = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);
        outMsg.setProperties(arg0.getProperties());
        String empty = "{}";
		// Translate message and publish
		SDTMap msgMap = arg0.getProperties();
		try {
			String action = msgMap.get("action").toString();
			String topicName = msgMap.get("topicName").toString();
			// Send out message
			if (action.equalsIgnoreCase("create")) {
				outMsg.setData(((BytesMessage)arg0).getData());
				prod.send(outMsg, JCSMPFactory.onlyInstance().createTopic(topicName));
			} else if (action.equalsIgnoreCase("update")) {
				outMsg.setData(((BytesMessage)arg0).getData());
				prod.send(outMsg, JCSMPFactory.onlyInstance().createTopic(topicName));
			} else if (action.equalsIgnoreCase("delete")) {
				
				outMsg.setData(empty.getBytes());
				prod.send(outMsg, JCSMPFactory.onlyInstance().createTopic(topicName));
			}
			arg0.ackMessage();
			
		} catch (SDTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JCSMPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
