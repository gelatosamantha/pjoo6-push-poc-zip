package com.solacesystems.jcsmp.samples.introsamples.intro;
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
// import com.solacesystems.jcsmp.XMLMessage;
import com.solacesystems.jcsmp.StreamMessage;
import com.solacesystems.jcsmp.MapMessage;


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
        Class targetClass ;
        BytesXMLMessage outMsg = null;


        if (arg0 instanceof XMLContentMessage) {
        	targetClass = XMLContentMessage.class;
        	outMsg = JCSMPFactory.onlyInstance().createMessage(XMLContentMessage.class);

            System.out.printf("BytesMessage received: '%s'%n",
                    ((XMLContentMessage)arg0).dump()+ " " + arg0.getClass().toString());
        }else if (arg0 instanceof BytesMessage) {
        	targetClass = BytesMessage.class;
        	outMsg = JCSMPFactory.onlyInstance().createMessage(BytesMessage.class);

            // System.out.println("Message received => " + arg0.getClass().toString() +  " == " + arg0.dump());
        }else if (arg0 instanceof MapMessage) {
        	targetClass = MapMessage.class;
        	outMsg = JCSMPFactory.onlyInstance().createMessage(MapMessage.class);        	
        }else if (arg0 instanceof StreamMessage) {
        	targetClass = StreamMessage.class;
        	outMsg = JCSMPFactory.onlyInstance().createMessage(StreamMessage.class);        	
        }else if (arg0 instanceof TextMessage) {
        	targetClass = TextMessage.class;
        	outMsg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        }else if (arg0 instanceof XMLContentMessage) {
        	targetClass = XMLContentMessage.class;
        	outMsg = JCSMPFactory.onlyInstance().createMessage(XMLContentMessage.class);
        }
            System.out.println("Message received => " + arg0.getClass().toString() +  " == " + arg0.dump());



        // XMLContentMessage outMsg = JCSMPFactory.onlyInstance().createMessage(XMLContentMessage.class);
        // BytesXMLMessage outMsg = JCSMPFactory.onlyInstance().createMessage(targetClass);
        outMsg.setProperties(arg0.getProperties());
        String empty = "{}";
		// Translate message and publish
		SDTMap msgMap = arg0.getProperties();
		try {
			String action = msgMap.get("action").toString();
			String topicName = msgMap.get("topicName").toString();
			// Send out message
			if (action.equalsIgnoreCase("create")) {
				// outMsg.writeBytes(((XMLContentMessage)arg0).getBytes());
				((TextMessage)outMsg).setText(((TextMessage)arg0).getText());
				// outMsg.writeBytes(empty.getBytes());
            // System.out.println("Out Msg => " + outMsg.getClass().toString() +  " == " + outMsg.dump());

				prod.send(outMsg, JCSMPFactory.onlyInstance().createTopic(topicName));
			} else if (action.equalsIgnoreCase("update")) {
				// outMsg.writeBytes(((TextMessage)arg0).getBytes());
				((TextMessage)outMsg).setText(((TextMessage)arg0).getText());
				prod.send(outMsg, JCSMPFactory.onlyInstance().createTopic(topicName));
			} else if (action.equalsIgnoreCase("delete")) {
				// outMsg.writeBytes(empty.getBytes());
				((TextMessage)outMsg).setText(empty);
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
