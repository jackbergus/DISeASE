package disease.configurations;

import disease.utils.wikipedia.model.interfaces.ITemplateResolver;

import org.sweble.wikitext.engine.utils.SimpleWikiConfiguration;

import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.NodeList;
import disease.utils.wikipedia.model.WikiToPlainTextWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;

public class DefaultConfs {

	public static SimpleWikiConfiguration config;
        private static String OS = System.getProperty("os.name").toLowerCase();
	static {
		try {
			config = new SimpleWikiConfiguration("classpath:/org/sweble/wikitext/engine/SimpleWikiConfiguration.xml");
		} catch (Throwable ex) {
                Logger.getLogger(DefaultConfs.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
        
        public static boolean isWindows() {
		return (OS.contains("win"));
	}
 
	public static boolean isMac() {
		return (OS.contains("mac"));
	}
 
	public static boolean isUnix() {
		return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0 );
	}
        
        public static String getNativeExtension() {
            if (isWindows())
                return "dll";
            else if (isUnix()) 
                return "so";
            else if (isMac()) 
                return "dylib";
            else 
                return null;
        }
        
        public static void setupConfigurations() {
            /*System.out.println("Setting HyperGraphDB path...");
            String native_path = "native"+File.separator;
            if (isWindows()) {
                native_path+="win";
            } else if (isMac()) {
                native_path+="mac";
            } else if (isUnix()) {
                native_path+="linux";
            } else {
                System.err.println("Your operative System not supported: "+OS);
                System.exit(1);
            }
            String libpath = System.getProperty("java.library.path");
            libpath = libpath + File.pathSeparator + native_path;
            System.setProperty("java.library.path",libpath);
            System.out.println(libpath);*/
        }
	
	public static String nodeToString(AstNode nl) {
		WikiToPlainTextWriter p = new WikiToPlainTextWriter();
		p.go(nl);
		return p.toString().toLowerCase();
	}
	
	public static String nodeToString(NodeList nl, ITemplateResolver itr) {
		WikiToPlainTextWriter p = new WikiToPlainTextWriter(itr);
		p.go(nl);
		return p.toString().toLowerCase();
	}
	
	public static String nodeToString(AstNode nl, ITemplateResolver itr) {
		WikiToPlainTextWriter p = new WikiToPlainTextWriter(itr);
		p.go(nl);
		return p.toString().toLowerCase();
	}
	
}
