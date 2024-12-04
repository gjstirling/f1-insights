# F1 Insights API 

An API built utilising the Scala Play framework, Scala's Mongo DB driver, the Akka fork (Pekko), containerised using Docker and deployed on Google Cloud Platform. 

Hosted on Google Cloud Platform BaseUrl: ```https://f1insights-283237385368.us-central1.run.app```

## Installation and Setup Instructions

Clone down this repository. You will need `Scala` and `sbt` installed globally on your machine.  

Create Binary: ```sbt clean compile stage dist``` 

## Endpoints

```/``` : A request on this end point will return a basic response "Welcome to the f1 insights API". <br>

```/events``` : A request on this end point will return a list of qualifying events <br>

```/drivers``` : A request on this end point will return a list of driver name's, numbers and teams they have raced for <br>

```/quali``` : Using the above routes you can build a request on this end point, which will return a list of hotlaps 
when given a driver's race number and session key (Pulled from events route). 
If no query params are given it will return with an error response. A list of qualifying events can be accessed from 
the /events endpoint <br>

## Below is an example request: 
### Request 
`https://f1insights-283237385368.us-central1.run.app/quali?driver_number=55&session_key=9570`

### Response

```JSON
[
  {
    "lap_number": 2,
    "sector_1": 32.861,
    "sector_2": 54.692,
    "sector_3": 30.978,
    "lap_time": "1m58.531"
  },
........
  {
    "lap_number": 22,
    "sector_1": 32.385,
    "sector_2": 51.619,
    "sector_3": 30.473,
    "lap_time": "1m54.477"
  },
  {
    "average_laptime": "1m55.598"
  }
]
```
**request template:** `https://myservice-m3p2yzv7ma-uc.a.run.app/quali?driver_number=[DriverNumber]&session_key=[SessionKey]`

## Docker: 
- set application secret <br>
  ```export SECRET="changeme"``` - Insert secret here <br> check is set with ```echo $SECRET```
- build image: <br>
```docker buildx build --platform linux/arm64 --build-arg SECRET=$SECRET -t [imageName] --load .```
- run container: <br>
```docker run --name [containerName] -d -p 9000:9000 -e PORT=9000 myimage```


## TODO list:
- Remove use of Mongo Client in each repository, inject mongo client directly
- Catagorise drivers/events/laps by year
- Create comparison endpoint for two drivers in a shared session
