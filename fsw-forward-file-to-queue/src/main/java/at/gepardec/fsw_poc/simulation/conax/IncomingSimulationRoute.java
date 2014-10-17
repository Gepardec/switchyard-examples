package at.gepardec.fsw_poc.simulation.conax;

import org.apache.camel.builder.RouteBuilder;

public class IncomingSimulationRoute extends RouteBuilder {

	/**
	 * The Camel route is configured via this method. The from endpoint is
	 * required to be a SwitchYard service.
	 */
	public void configure() {

		from("switchyard://IncomingFile")
				.convertBodyTo(String.class)
				.log("Simulation Received message for 'IncomingFile' : ${body}")
				.to("switchyard://IncomingQueue");
	}

}
