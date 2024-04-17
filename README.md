# text-matcher
Text Matcher System for BigID

**Run:**
To run the application using that jar file through command: 

	java -jar text-matcher-1.0.jar

You can use the below arguments to configure the Text Matcher.

If there is no arguments then the system will use the default configuration.

**Configruations:**

**arg[0]** - Search Keywords (Default: James,John,Robert,Michael,William,David,Richard,Charles,Joseph,Thomas,Christopher,Daniel,Paul,Mark,Donald,George,Kenneth,Steven,Edward,Brian,Ronald,Anthony,Kevin,Jason,Matthew,Gary,Timothy,Jose,Larry,Jeffrey,Frank,Scott,Eric,Stephen,Andrew,Raymond,Gregory,Joshua,Jerry,Dennis,Walter,Patrick,Peter,Harold,Douglas,Henry,Carl,Arthur,Ryan,Roger)

**arg[1]** - Lines to read for batch (Default: 1000)

**arg[2]** - Matcher workers (Default: 5)

**arg[3]** - Case sensitve (Default: true)

**Eg:**
	
	java -jar text-matcher-1.0.jar Timothy,Arthur,Robert,brian 5000 8 false

**Note:**
	I have used java 17 to develop this application