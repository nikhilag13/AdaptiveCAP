

nohup java -cp /app/grpctest-1.0-SNAPSHOT-jar-with-dependencies.jar org.sjsu.grpcCommunicator.InitScript &

sleep 30

nohup java -cp /app/grpctest-1.0-SNAPSHOT-jar-with-dependencies.jar org.sjsu.grpcCommunicator.StageTwoStart > stage2.out

bash


