# Scraper

<h5>Technical Assessment Task for Finology

### Task Requirements

* A web crawler that starts from [this address](http://magento-test.finology.com.my/breathe-easy-tank.html) and extract product informations.
* Extracted information should be saved in an SQLite database.
* Extracted data must also be printed on the console.
* Product data must contain :
    1. Name of the Product
    2. Price
    3. The details or description of the product
    4. Extra information about the product, as a delimited list
* Saved/printed products must be unique.

### Implementation
There are two implementations provided in this repository on two branches: [Synchronously](https://github.com/amaralani/finology-scraper) (<code>main</code> branch) and [Asynchronously](https://github.com/amaralani/finology-scraper/tree/async).

The async way was my first idea, but since sometimes the test application became unresponsive under load, I decided to add the sync solution as main. This should be said that the solution using Spring Application Events could be implemented synchronously too.

Both of the implementations use :
* Spring Boot
* Spring Data JPA
* Spring Data Redis to interact with Redis
* Redis Used to cache fetched addresses
* [JSoup](https://github.com/jhy/jsoup) Used to parse HTML pages 
 
The asynchronous implementation uses Spring Application Events to fetch the addresses in parallel, but the synchronous 
implementation uses a BlockingQueue to handle the addresses one by one. 

In case of <code>HTTP status 5XX</code> or a <code>SocketTimeoutException</code> the request will back-off for two minutes and retry for up to 5 times.

The application starts scanning the site after receiving a post request to <code>/scan</code>.

### Extras
* Start path is by default set to the [given link](http://magento-test.finology.com.my/breathe-easy-tank.html), but the body posted to <code>/scan</code> could contain any other address to start from.
* Through a property in <code>application.properties</code> the logger could be set to either <code>CONSOLE</code> or <code>FILE</code>.
When set to <code>CONSOLE</code> the fetched product gets printed in the console. When set to <code>FILE</code>, during application startup a log file is created and the fetched products are printed there.

### Building

First of all we need to build the <code>JAR</code> file:

    ./mvnw clean package

From there, we can use run the project in two ways:

#### Standalone
The application can be run in an IDE, or by running it as a single <code>JAR</code> file:

    java -jar finology-scraper-0.0.1-SNAPSHOT.jar -Dspring.profiles.active=standalone

Although when running it this way, we must provide the Redis service separately. An optional <code>docker-compose-redis.yml</code> is provided for this:

     docker-compose -f Docker/docker-compose-redis.yml up

#### Docker compose
With the prepared <code>JAR</code> file we can create the docker image:   

    docker build . -t finology-scraper

After that we can simply run the docker-compose file:

    docker-compose -f Docker/docker-compose.yml up
    
### Endpoints
There are two end-points exposed in this application.

#### <code>/scan</code> 
* Accepts a <code>POST</code> request
* Content type must be <code>application/json</code> 
* Body should not be empty, although an empty JSON object is acceptable.
* Body may contain a <code>path</code> indicating the path to start scanning from.
* If successful, results in a <code>202 ACCEPTED</code> response.

Sample :
````
POST http://localhost:8080/scan
Content-Type: application/json

{
  "path" : "http://magento-test.finology.com.my/breathe-easy-tank.html"
}
````
#### <code>/product/list</code>
* Accepts <code>GET</code> requests.
* Responds in <code>application/json</code>
* The result will be a list of products.

Sample Response:
````
{
  "products": [
    {
      "name": "Breathe-Easy Tank",
      "price": "34",
      "description": "The Breathe Easy Tank is so soft, lightweight, and comfortable, you won't even know it's there -- until its high-tech Cocona® fabric starts wicking sweat away from your body to help you stay dry and focused. Layer it over your favorite sports bra and get moving. • Machine wash/dry. • Cocona® fabric.",
      "extraInformation": "Style: Tank | Material: Cocona® performance fabric, Cotton | Pattern: Solid | Climate: Indoor, Warm ",
      "link": "https://magento-test.finology.com.my/breathe-easy-tank.html"
    },
    {
      "name": "Gabrielle Micro Sleeve Top",
      "price": "28",
      "description": "Luma's most popular top, the Gabrielle Micro Sleeve is back with even more comfort and style. • Lime green v-neck tee. • Slimming, flattering fit. • Moisture-wicking, quick-drying, anti-microbial, and anti-odor construction. • Longer curved hem provides additional coverage. • 55% Hemp / 45% Organic Cotton.",
      "extraInformation": "Style: Tee | Material: Cotton, Hemp | Pattern: Solid | Climate: Indoor, Warm ",
      "link": "https://magento-test.finology.com.my/gabrielle-micro-sleeve-top.html"
    }
  ]
}
````
### Caveats
* When running the synchronous implementation, the <code>/scan</code> request will not be processed more than once. If for any reason we want to start scanning another address, the application must be restarted.
* When running with docker-compose and using file logger, the file would not be easily accessible. 

### Future work
* Rate limiting
* Realtime web display of the fetched products 
* Add CodeCov and Travis CI