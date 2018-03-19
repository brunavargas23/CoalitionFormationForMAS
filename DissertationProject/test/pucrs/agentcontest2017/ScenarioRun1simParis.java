package pucrs.agentcontest2017;

import org.junit.Before;
import org.junit.Test;

import jacamo.infra.JaCaMoLauncher;
import massim.Server;


public class ScenarioRun1simParis {
	@Before
	public void setUp() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Server.main(new String[] {"-conf", "conf/1simConfigParis_12ag.json", "--monitor"});	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		try {			
			JaCaMoLauncher.main(new String[] {"pucrs-mapc2017_12ag.jcm"});
		} catch (Exception e) {
			System.out.println("Exception MAS: "+e.getMessage());
			e.printStackTrace();
		} 

	}

	@Test
	public void run() {
	}

}
