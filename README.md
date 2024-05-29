# F1 Insights API 

This is a RESTful API built utilising the Scala Play framework, Scala's Mongo DB driver and the Akka fork (Pekko). 

## Installation and Setup Instructions

Clone down this repository. You will need `Scala` and `sbt` installed globally on your machine.  

## Endpoints

```/quali``` : A request on this end point will return a list of hotlaps when given a driver's last name and 2024 season location short name. If no query params are given it will return with an error response. <br>

## Below is an example request: 
### Request 
`localhost:9000/quali?driver_last_name=Sainz&event_name=Monaco`

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

**request template:** `localhost:9000/quali?driver_last_name=[DriverName]&event_name=[EventName]`

### Driver last names 
    Verstappen    Sargeant    Ricciardo    Norris    Gasly
    Perez         Alonso      Leclerc      Stroll    Magnussen
    Hulkenberg    Tsunoda     Albon        Zhou      Ocon
    Hamilton      Sainz       Russell      Bottas    Piastri

### Event names 
    Sakhir      Jeddah    Melbourne    Suzuka   
    Shanghai    Miami"    Imola        Monaco     

## Testing 
To run tests use the following:  <br/> `sbt clean coverage test` <br/>
<br/> To view test coverage run the following commands: 
<br/> `sbt clean coverage test` 
<br/> `sbt coverageReport` <br/>

To view the report navigate to the Index.html file located inside the target directory form path: 
<br/> `/target/scala-2.13/scoverage-report/index.html`

## TODO list: 
- Deploy /quali event point to AWS server 

