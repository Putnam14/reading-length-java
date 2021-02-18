# Reading Length
Cool badges:
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3c2929ad15344b5eb43119f0c997771c)](https://app.codacy.com/gh/Putnam14/reading-length-java?utm_source=github.com&utm_medium=referral&utm_content=Putnam14/reading-length-java&utm_campaign=Badge_Grade)
[![CodeFactor](https://www.codefactor.io/repository/github/putnam14/reading-length-java/badge)](https://www.codefactor.io/repository/github/putnam14/reading-length-java)

## Description
Reading Length is a website where people can find out how long it might take them to read a book.

[Live website](https://www.readinglength.com)

## Contributing
Feel free to open up an issue, or a pull request for an existing issue!

## Running the webservices
You can run the webservices locally by using maven goals.
`mvn clean mn:run -pl <WEBSERVICE NAME>`

## Deploying to GCP
Reading Length is built to run on Google Cloud Platform.

To deploy the webservices to Google Cloud Run, run the following command in the webservice's folder:
`mvn com.google.cloud.tools:jib-maven-plugin:1.8.0:build \
     -Dimage=gcr.io/<PROJECT>/<IMAGE>`
     
The above command will package the webservice using JIB and push it to your GCP image repository. Run it on Cloud Run by creating a new service referencing that image.