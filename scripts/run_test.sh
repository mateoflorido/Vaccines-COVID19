#!/bin/bash
# Our custom function
cust_func(){
	cat sample | /usr/lib/jvm/java-1.11.0-openjdk-amd64/bin/java -javaagent:/home/mateo/.local/share/JetBrains/Toolbox/apps/IDEA-U/ch-0/202.7660.26/lib/idea_rt.jar=35725:/home/mateo/.local/share/JetBrains/Toolbox/apps/IDEA-U/ch-0/202.7660.26/bin -Dfile.encoding=UTF-8 -classpath /home/mateo/Vaccines-COVID19/src/EPSClient/target/classes org.flosan.EPSClient.view.EPSClientExec

}
# For loop 5 times
for i in {1..100}
do
	cust_func & # Put a function in the background
done
 
## Put all cust_func in the background and bash 
## would wait until those are completed 
## before displaying all done message
wait 
echo "All done"
