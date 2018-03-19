package actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;


public class recordStatistics extends DefaultInternalAction {
	private static final long serialVersionUID = 5444574937658709099L;
	
	private final String DIR_EXPERIMENT = "/experiment/temp/";
	
	@Override
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		String category = args[0].toString();
		
		String line 		= "";
		String fileName 	= "";
		String head 		= "";
		
		if (category.equals("jobInterval")) {
			fileName = "jobInterval";
			line = args[1].toString()+",";
			head = "job interval\n";
		} else if (category.equals("endRoundTeam")){			
			fileName = "roundTeam";
			for (int i = 1; i < args.length; i++) {
				line+=args[i].toString()+",";
			}
			head = "money,received jobs,completed jobs,failed jobs,free agents failed,job evaluation failed,failed coalition,failed TA,Shops,Workshops,Storages\n";
		} else if (category.equals("endRoundIndividual")){			
			fileName = "roundIndividual";
			for (int i = 1; i < args.length; i++) {
				line+=args[i].toString()+",";
			}
			head = "agent,type,no actions,worked jobs,failed jobs,earned money\n";		
		} else if(category.equals("step")){
			fileName = "step";
			line = args[1].toString()+","+args[2].toString()+",";
			head = "steps,money\n";
		}
		
//		saveFile(nameOfFile,line);
		saveCSV(fileName, head, line);
	
        return true;
	}
	
	public void saveCSV(String fileName, String head, String data)
	{
		PrintWriter pw;
		boolean append = true;
		try {
			StringBuilder sb = new StringBuilder();
			
			File file = new File(System.getProperty("user.dir")+DIR_EXPERIMENT+fileName+".csv");
			synchronized (file) {
		    	if (!file.getParentFile().exists())
		    		file.getParentFile().mkdirs();
		    	
		    	if(!file.exists()){
		    		file.createNewFile();
		    	   
		    		sb.append(head);
		    	}
		    	
		    	sb.append(data);
		        sb.append('\n');
		        
				pw = new PrintWriter(new FileWriter(file,append)); 
		        pw.write(sb.toString());
		        pw.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
   }
	public void saveFile(String fileName, String data)
	{
		try{
	    	String content = data;
	    	File file =new File(System.getProperty("user.dir")+DIR_EXPERIMENT+fileName+".txt");
	    	System.out.println("FIle: "+file.getAbsolutePath());
	
	    	if (!file.getParentFile().exists())
	    		file.getParentFile().mkdirs();
	    	
	    	if(!file.exists()){
	    		System.out.println("file doesnt exists");
	    	   file.createNewFile();
	    	   System.out.println("file created");
	    	}
	
	    	FileWriter fw = new FileWriter(file,true);
	    	BufferedWriter bw = new BufferedWriter(fw);
	    	bw.write(content);
	    	bw.newLine();
	    	bw.flush();
	
	    	bw.close();
		}catch(IOException ioe){
			System.out.println("Exception occurred:");
			ioe.printStackTrace();
		}
   }
}
