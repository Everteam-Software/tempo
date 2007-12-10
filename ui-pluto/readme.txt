CONTENTS
This contains almost a vanilla pluto install. Files are exploded so it's easy to customize it.
- standard-1.0.6.jar file slightly modified (removed the descriptions so no exception is thrown)

PLUTO INSTALL

we need to install the following dependencies in the common libs of the server:
- xalan
- a set of pluto related components:
	- pluto-container 
	- pluto-descriptor-api
	- pluto-descriptor-impl
	- pluto-taglib
- others as described in geronimo-web.xml ...
	
Then we can access pluto at the following url:
http://localhost:8080/pluto/login.jsp

PLUTO PORLET DEVELOPMENT