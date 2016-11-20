# Dev Test (Bus Route Challenge) Implementation

### Assumptions
* In a given route, the stations are connected in the forward direction(not bidirectional).
* The Application/Server will be restarted manually(or by the external script) if the input data file changes.

### Tech Stack
* Spring Boot
* Jetty
* JUnit/Mockito

### Application Flow
```
                                                                                   __________________     
                                                                                  |                  |
                                                                                  | In-Memory Cache  | 
                                                                                  |__________________|     
                                                                                           ^
                                                                                           |    
                                                                                           |
    _____________________          _______________         ________________         ________________   
   |                     |        |               |       |                |       |                |              
   | Browser/REST Client |  --->  | REST EndPoint | --->  | Search Service | --->  | Cache Manager  | 
   |_____________________|        |_______________|       |________________|       |________________|      
                                                                                           ^
                                                                                           |    
                                                                                           |
                                                            _____________         ____________________     
                                                           |             |       |                    |
                                                           | CLI/Service |  ---> | Application Loader | 
                                                           |_____________|       |____________________| 
                                            
```
* Application/Server will fail to start if
   * input data file path is not provided
   * input data file is not accessible
   * input data file content is not provided as per the specification
* Bus route data will be stored in the in-memory data cache while application is loading.
* REST Endpoint will be available for service once the application/server is successfully started.

### Design Considerations
* Extracted from the code comments.
```
    /**
     * DATA STRUCTURE DESIGN CHOICES
     * -----------------------------
     * REQUIREMENT: 
     * -----------
     *      (1) "Quickly answer if two given stations are connected by a bus route"
     *      (2) The input data file
     *           - Loaded during the application/server start only and any changes to this file will requires a restart.
     *           - Changes weekly (which is not too often)
     *
     * GOAL:
     * -----
     *      - The search query should return the response which has the time complexity
     *        close to O(1) to provide a better UX and future scaling needs.
     * 
     * TRADE-OFFs:
     * ----------
     *      (1) Data cache population time - It should good since it is being populated only once.
     *      (2) Memory to cache data       - As per the requirement, In worst case scenario,
     *                                       it can't go beyond, 100K routes with 1000 stations each.
     *                                       And Hardware is relatively cheap.
     * 
     * OPTION (1): Map<DEPARTURE-STATION, Set<CONNECTED-STATIONS>>
     * ---------------------------------------------------------
     *      - For each station, the directly connected stations in all the routes will be added in a hash based set.
     *      - Search Time Complexity is O(1) + O(1)
     *           - (1) Search if the Departure Station exists
     *           - (2) Search if the Arrival Station is connected
     *
     *      - This data structure works for the current requirement. 
     *      - However, this option will not scale for potential future requirement extension.
     *      - If the connected routes are required, this data structure will not help.
     * 
     * OPTION (2): Map<DEPARTURE-STATION, Map<CONNECTED-STATION, List<CONNECTED-ROUTES>>>
     * ----------------------------------------------------------------------------------
     *      - Same as the OPTION (1) and in addition to that, all routes which connect these two stations
     *        will be stored in a list.
     *      - Search Time Complexity is similar to OPTION (1).
     *
     * OPTION (2) is being used in this implementation to store the data in-memory
     */
```

### Testing
* Unit tests and Integration tests will be run during the build/package process.
* If the test fails, the application won't be packaged.

### Other Notes
* Logs are printed on the console and not intentionally redirected it to file.
* Exception and validation messages can be enabled for translations.
* Interfaces are intentionally ignored to keep the project simple.

### Compliance
* The git clone option while building the docker image is failing at step 9. Hence, tested it after download the repository as a zip archive. Here are the snippets

````
sivaa-MAC:tests sivaa$ bash build_docker_image.sh dev-test-bus-route-challenge-master.zip 
	zip mode
	Sending build context to Docker daemon 2.403 MB
	Step 1 : FROM ubuntu:16.04
	 ---> e4415b714b62
	Step 2 : RUN apt-get update && apt-get install -y     openjdk-8-jdk     git     maven     gradle     unzip  && rm -rf /var/lib/apt/lists/*
	 ---> Running in 081f5888e00f
	Get:1 http://archive.ubuntu.com/ubuntu xenial InRelease [247 kB]
	Get:2 http://archive.ubuntu.com/ubuntu xenial-updates InRelease [95.7 kB]


	 <REMOVED>


	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time: 05:39 min
	[INFO] Finished at: 2016-11-20T15:37:10+00:00
	[INFO] Final Memory: 33M/197M
	[INFO] ------------------------------------------------------------------------
	/data/test
	 ---> e3214ff099d4
	Removing intermediate container 4bd7efc25427
	Step 13 : CMD bash /scripts/run.sh
	 ---> Running in 69d7f78c122d
	 ---> 6089f8dda3ed
	Removing intermediate container 69d7f78c122d
	Successfully built 6089f8dda3ed
	sivaa-MAC:tests sivaa$ docker images
	REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
	dev-test            latest              6089f8dda3ed        32 seconds ago      773.1 MB
	ubuntu              16.04               e4415b714b62        3 days ago          128.1 MB
	sivaa-MAC:tests sivaa$ bash run_test_docker.sh
	TEST PASSED!
	sivaa-MAC:tests sivaa$ 

````
