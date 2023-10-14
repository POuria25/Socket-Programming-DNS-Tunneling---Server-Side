all:
	javac *.java
clean:
	rm *.class
server:
	javac Base32.java	HttpGet.java	Query.java	Server.java	Clienthandler.java	Header.java
	java Server tnl.test
client:
	java -jar digger.jar localhost 1030 https://example.com tnl.test
