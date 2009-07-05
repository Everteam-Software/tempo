package org.intalio.sita;

import java.io.File;
import java.io.FilenameFilter;



public class AutomaticStartupProcessFilter implements FilenameFilter{



	@Override
	public boolean accept(File dir, String name) {
		File f= new File(dir.getAbsoluteFile()+"/"+name);
		if(f.getName().endsWith(".pipa")  ){
			File[] childs=f.getParentFile().listFiles();
			boolean result=false;
			for(File child:childs){
				if(child.getName().equals(f.getName()+".auto-startup"))result=true;
				if(child.getName().endsWith("NOSTARTUP"))return false;
			}
		return result;
		}
		return false;
	}

}
