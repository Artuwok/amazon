***Steps to run the application****

1. navigate to directory of the project and run the sbt package command
 a) cd <path-of application>
 b) sbt package

2. Navigate to spark folder and run

a) cd <path to spark folder> 
b) ./bin/spark-submit \
   –master spark://localhost:7077 \
   –class amazon.Amazon \
   /home/art/Desktop/spark-1-3-0_2.11-1.0.jar  // path to compiled jar