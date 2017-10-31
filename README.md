# CarbaResist

CarbaResist is a bioinformatics tool developed for my capstone project in BIOC435 for the 2017 fall semester. Frameworks used include BioJava for handling the local alignments of proteins, with RabbitMQ used for sending analysis jobs to the job processor, and Couchbase for storing the job information and results. Spring Boot is used to handle Web requests to the job submission service, as well as injecting and managing the connections to RabbitMQ and Couchbase.

# Running

You will need to adjust your `application.properties` file to reflect your environment. Here is a sample `application.properties` file:

    rabbitmq.queueName=carbaresist
    rabbitmq.host=localhost
    
    couchbase.hosts=localhost
    couchbase.bucket=carbaresist
    couchbase.username=carbaresist
    couchbase.password=Password

    carbaresist.substitutionMatrix=blosum65

    spring.main.web-environment=false # Necessary if you run your job submission service on the same system as your job processor, also the processor has no need for Tomcat to be running.
    
All dependencies are managed by Maven, but you will need to ensure that Couchbase and RabbitMQ are running somewhere in your network and that the job submission service and the job processor can access both.
