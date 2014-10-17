package at.gepardec.fsw_poc.simulation.conax;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.test.mixins.PropertyMixIn;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = SwitchYardTestCaseConfig.SWITCHYARD_XML, mixins = {
		CDIMixIn.class, PropertyMixIn.class, HornetQMixIn.class })
public class IncomingFileTest {

	public static final String RESULT_QUEUE = "resultQueue";

	private static final String SIMULATION_DIR = "target/simulation";
	private static final String INPUT_DIR = SIMULATION_DIR + "/input";

	private SwitchYardTestKit testKit;
	private HornetQMixIn hornetQMixIn;
	private PropertyMixIn propMixIn;

	@ServiceOperation("IncomingFile")
	private Invoker service;


	@BeforeDeploy
	public void setTestProperties() {
		propMixIn.set("fsw.poc.simulation.dir", SIMULATION_DIR);
	}

	@Test
	public void testMessageFromFileIsSentToQueue() throws Exception {

		copyToDir("test1.xml", INPUT_DIR);

		hornetQMixIn.readJMSMessageAndTestString(
				receiveMessage("incomingQueue"),
				testKit.readResourceString("test1.xml"));
	}

	protected Message receiveMessage(String queueName) throws Exception {

		Session session = hornetQMixIn.createJMSSession();
		final MessageConsumer consumer = session.createConsumer(HornetQMixIn
				.getJMSQueue(queueName));
		Message message = consumer.receive(3000);
		assertNotNull("Got message", message);
		if (message instanceof TextMessage) {
			System.out.println("TextMessage");
		} else if (message instanceof ObjectMessage) {
			System.out.println("ObjectMessage");
		} else if (message instanceof BytesMessage) {
			System.out.println("BytesMessage");
		} else {
			System.out.println("Message has type "
					+ message.getClass().getName());
		}
		return message;
	}

	protected void copyToDir(String file, String dir) throws IOException {
		String msg = testKit.readResourceString(file);
		FileUtils.writeStringToFile(new File(dir + '/' + file), msg);
	}

}
