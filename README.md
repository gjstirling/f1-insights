# F1 Insights API 

This is a RESTful API built utilising the Scala Play framework, Scala's Mongo DB driver and the Akka fork (Pekko). 

## Installation and Setup Instructions

Clone down this repository. You will need `Scala` and `sbt` installed globally on your machine.  

## Testing 
To run tests use the following:  `sbt clean coverage test`
To view test coverage run the following commands: 
`sbt clean coverage test`
`sbt coverageReport`

To view the report navigate to the Index.html file located inside the target directory form path: `/target/scala-2.13/scoverage-report/index.html`

## TODO list: 
- Finish testing of Event Repository 
- Finish testing of Services and ApiClient
- Complete /Quali endpoint to return filtered lap data
- Much local deployment to production enviroment

## Learning/Reflections: 
