package pucrs.agentcontest2017;

import org.junit.Before;
import org.junit.Test;

import jacamo.infra.JaCaMoLauncher;
import massim.Server;


public class ScenarioRun1simParis_Automatic {
	@Before
	public void setUp() {
		String confFileName = System.getProperty("confName");
		
		String pathToConfFile = "conf/"+confFileName+".json";
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Server.main(new String[] {"-conf", pathToConfFile});	
					System.out.println("Server finished");
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
		} catch (Error er) {
			er.printStackTrace();
		}
	}

	@Test
	public void run() {
	}
}
