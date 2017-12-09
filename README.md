# CarbaResist

CarbaResist is a bioinformatics tool developed for my capstone project in BIOC435 for the 2017 fall semester. Frameworks used include BioJava for handling the local alignments of proteins, with RabbitMQ used for sending analysis jobs to the job processor, and Couchbase for storing the job information and results. Spring Boot is used to handle Web requests to the job submission service, as well as injecting and managing the connections to RabbitMQ and Couchbase.

CarbaResist is split into two components: CarbaResist-Web and CarbaResist-Process. The CarbaResist-Web component provides a RESTful API as well as an Angular frontend to submit job requests with. The CarbaResist-Process component will receive job requests from a RabbitMQ queue, process them, and then send them back to the CarbaResist-Web component for storage into the database.

# Dependencies

Recent versions of RabbitMQ and Couchbase are required.

In Couchbase, you will have to add indicies to the email and jobId fields so that the Spring repositories can get records.

# Running

You will need to adjust your `application.properties` file to reflect your environment. Here is a sample `application.properties` file:

    rabbitmq.queueName=carbaresist
    rabbitmq.host=localhost
    
    couchbase.hosts=localhost
    couchbase.bucket=carbaresist
    couchbase.username=carbaresist
    couchbase.password=Password

    carbaresist.substitutionMatrix=blosum65 # Needing to remove in the future as the substitution matrix is now selectable on submission of jobs.

    spring.main.web-environment=false # Necessary if you run your job submission service on the same system as your job processor, also the processor has no need for Tomcat to be running.
    
All dependencies are managed by Maven, but you will need to ensure that Couchbase and RabbitMQ are running somewhere in your network and that the job submission service and the job processor can access both.

To start the application after compilation, all that should be needed to run the CarbaResist-Web component is: `java -jar CarbaResist-<version of release>.jar --spring.config.name=<name of properties file>`. It is extremely similar for the CarbaResist-Process component, outside of the JAR having the prefix `CarbaResist-Process` instead of just `CarbaResist`.

It is highly recommended that these two components run on different virtual or physical machines due to the CarbaResist-Process requiring a lot of processing power and memory to process jobs. The CarbaResist-Web component doesn't need as much resources, so it can be run on a much less powerful virtual or physical machine.

This project was designed with Unix-based systems in mind, but should be abstracted enough as to run on Windows systems as well. It is also highly recommended that you proxy the CarbaResist-Web component instead of exposing it directely to the internet due to security and performance reasons.
