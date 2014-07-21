How to fix linking error with dynamic libraries:
	http://stackoverflow.com/questions/6383310/python-mysqldb-library-not-loaded-libmysqlclient-18-dylib
	sudo install_name_tool -change libz3.dylib /Work/Eclipse/workspace/scala/VarAnalysis-Tool/z3_plugin/lib/libz3.dylib /Work/Eclipse/workspace/scala/VarAnalysis-Tool/z3_plugin/lib/libz3java.dylib
	
	In com.microsoft.z3.Native.java @ Line 10:
		Change	 	System.loadLibrary("z3java"); 
	  		into	System.load("/Work/Eclipse/workspace/scala/VarAnalysis-Tool/z3_plugin/lib/libz3java.dylib");