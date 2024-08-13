# F1 Insights API 

An API built utilising the Scala Play framework, Scala's Mongo DB driver, the Akka fork (Pekko), containerised using Docker and deployed on Google Cloud Platform. 

Hosted on Google Cloud Platform BaseUrl: ```https://myservice-m3p2yzv7ma-uc.a.run.app```

## Installation and Setup Instructions

Clone down this repository. You will need `Scala` and `sbt` installed globally on your machine.  

```sbt clean compile stage dist```

## Endpoints

```/``` : A request on this end point will return a basic response "Welcome to the f1 insights API". <br>

```/events``` : A request on this end point will return a list of qualifying events in JSON as a response <br>

```/quali``` : A request on this end point will return a list of hotlaps when given a driver's last name and 2024 season location short name. If no query params are given it will return with an error response. A list of qualifying events can be accessed from the /events endpoint <br>

## Below is an example request: 
### Request 
`https://myservice-m3p2yzv7ma-uc.a.run.app/quali?driver_last_name=Sainz&event_name=Monaco`

### Response

```JSON
[
   {
      "lap_number": 10,
      "sector_1": 18.679,
      "sector_2": 33.614,
      "sector_3": 19.25,
      "lap_time": "1m11.543"
   },
   {
      "lap_number": 13,
      "sector_1": 18.621,
      "sector_2": 33.616,
      "sector_3": 18.94,
      "lap_time": "1m11.177"
   },
   {
      "lap_number": 16,
      "sector_1": 18.669,
      "sector_2": 33.543,
      "sector_3": 18.863,
      "lap_time": "1m11.075"
   }
]
```

**request template:** `https://myservice-m3p2yzv7ma-uc.a.run.app/quali?driver_last_name=[DriverName]&event_name=[EventName]`

## Testing 
To run tests use the following:  <br/> `sbt clean coverage test` <br/>
<br/> To view test coverage run the following commands: 
<br/> `sbt clean coverage test` 
<br/> `sbt coverageReport` <br/>


<img width="1078" alt="TestCoverage" src="https://github.com/gjstirling/f1-insights/assets/85582990/256b47fa-1889-416c-886f-aa503331cff4">

To view the report navigate to the Index.html file located inside the target directory form path: 
<br/> `/target/scala-2.13/scoverage-report/index.html`

## Docker: 
- set application secret <br>
  ```export SECRET="changeme"``` - Insert secret here <br> check is set with ```echo $SECRET```
- build image: <br>
```docker buildx build --platform linux/arm64 --build-arg SECRET=$SECRET -t [imageName] --load .```
- run container: <br>
```docker run --name [containerName] -d -p 9000:9000 -e PORT=9000 myimage```


## TODO list:
- update quali route to access data from mongodb collection and filter by building a query. 
- create new route to compare drivers fastest laps and times they were completed
- Working on dividing these laps into sessions q1 q2 q3 etc.. 
