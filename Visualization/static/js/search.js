
var map;
var markers = [];
var restaurantResponse = "";
// var seriesDataOrlando = [];
// var seriesDataAlbany = [];
// var seriesCuisiniesOrlando = [];
// var seriesCuisiniesAlabany = [];
// var seriesAvgCostOrlando = [];
// var seriesAvgCostAlbany = [];
// var seriesRatingsOrlando = [];
// var seriesRatingsAlbany = [];
var seen = false;

function onLoadHandler() {

    clearMarkers();
    const list = document.getElementById("restaurant-list");
    const location = document.getElementById("location").value;
    // console.log("given locations is " + location);
    const data = {
        location: location
    };

    fetch("/getRestaurants", {
        method: "POST",
        body: JSON.stringify(data),
        headers: {
           'Content-Type': 'application/json'
        }
    })
        .then(r => r.json())
        .then(response => {
            if (Array.isArray(response.restaurants)) {
                console.log("inside search.js");
                console.log('Success:', JSON.stringify(response));
                restaurantResponse = response.restaurants;
                appendRestaurants(list, response.restaurants, location);
            }
            else if (response.message === "Please enter valid location!") {
                while (list.firstChild) {
                    list.removeChild(list.firstChild);
                }
            }
        }).catch(error => {
            console.error('Error:', error)
        });
}

function getUsername() {

    var email = sessionStorage.getItem("useremail");
    console.log(email);

    fetch("/getDetails/" + email, {
        method: "GET",
    })
        .then(r => r.json())
        .then(response => {
            if (response.message === "Success") {
                const sidebarusenamefield = document.getElementById('uname');
                const topbarusernamefield = document.getElementById('unameTopbar');
                sidebarusenamefield.innerText = response.username;
                topbarusernamefield.innerText = response.username;
            }
            else {
                window.alert("Unable to get User details!");
            }
            console.log('Success:', JSON.stringify(response));
        }).catch(error => {
            console.error('Error:', error)
        });
}

// function searchSubmit() {
//     clearMarkers();
//     const list = document.getElementById("restaurant-list");
//     const restaurantname = document.getElementById("location").value;
//     console.log("given name is " + restaurantname);
//     // const data = {
//     //     location: location
//     // };

//     fetch("/showRestaurantbyName/" + restaurantname, {
//         method: "GET"
//         //body: JSON.stringify(data),
//         //headers: {
//         //    'Content-Type': 'application/json'
//         //}
//     })
//         .then(r => r.json())
//         .then(response => {
//             if (Array.isArray(response.restaurants)) {
//                 console.log("inside search.js");
//                 console.log('Success:', JSON.stringify(response));
//                 restaurantResponse = response.restaurants;
//                 appendRestaurants(list, response.restaurants, restaurantname);
//             }
//             else if (response.message === "Please enter valid location!") {
//                 while (list.firstChild) {
//                     list.removeChild(list.firstChild);
//                 }
//             }
//         }).catch(error => {
//             console.error('Error:', error)
//         });
// }

function appendRestaurants(node, mainarray, location) {
    //Display only first 50 restaurants
    if(mainarray.length > 50){
        var array = mainarray.slice(0, 1000);
    }
    // var seriesData = []; //array as a map for highcharts
    const rating = parseFloat(document.getElementById("selectRating").value);
    const cuisine = (document.getElementById("selectCuisine").value).toLowerCase();
    while (node.firstChild) {
        node.removeChild(node.firstChild);
    }
    let nodeTemplate = "";
    // var orlandoCuisines = {};
    // var AlabanyCuisines = {};
    // var avgCostOrlando = {};
    // var avgCostAlbany = {};
    // var orlandoRatings = {};
    // var AlabanyRatings = {};

    array.forEach(item => {
        var address = item.Address.toLowerCase();
        var itemRating = parseFloat(item.rating);
        var cuisineList = (item.Cuisines.toLowerCase()).split(", "); //array of cuisines
        // var ratingList = (item.Rating_text.toLowerCase()).split(", "); //array of RatingList
        // // if(seen == false){
        // if (item.City == 'Orlando' && !seriesDataOrlando.includes(item.Name)) {
        //     seriesDataOrlando.push([item.Name, item.rating]);
        // }
        // else if (item.City == 'Albany' && !seriesDataAlbany.includes(item.Name)) {
        //     seriesDataAlbany.push([item.Name, item.rating]);
        // }

        // if (item.City == 'Orlando') {
        //     if (item.average_cost != "0") {
        //         if (item.average_cost in avgCostOrlando) {
        //             avgCostOrlando[item.average_cost] = avgCostOrlando[item.average_cost] + 1;
        //         } else {
        //             avgCostOrlando[item.average_cost] = 1;
        //         }
        //     }

        // }

        // if (item.City == 'Albany') {
        //     if (item.average_cost != "0") {
        //         if (item.average_cost in avgCostAlbany) {
        //             avgCostAlbany[item.average_cost] = avgCostAlbany[item.average_cost] + 1;
        //         } else {
        //             avgCostAlbany[item.average_cost] = 1;
        //         }
        //     }

        // }

        //Rating list populate

        // if (item.City == 'Orlando') {
        //     var i;
        //     for (i = 0; i < ratingList.length; i++) {
        //         if (ratingList[i].trim() != "") {
        //             if (ratingList[i] in orlandoRatings) {
        //                 orlandoRatings[ratingList[i]] = orlandoRatings[ratingList[i]] + 1;
        //             } else {
        //                 orlandoRatings[ratingList[i]] = 1;
        //             }
        //         }
        //     }

        // } else if (item.City == 'Albany') {
        //     var i;
        //     for (i = 0; i < ratingList.length; i++) {
        //         if (ratingList[i].trim() != "") {
        //             if (ratingList[i].trim() in AlabanyRatings) {
        //                 AlabanyRatings[ratingList[i]] = AlabanyRatings[ratingList[i]] + 1;
        //             } else {
        //                 AlabanyRatings[ratingList[i]] = 1;
        //             }
        //         }
        //     }

        // }

        //cuisineList populate

        // if (item.City == 'Orlando') {
        //     var i;
        //     for (i = 0; i < cuisineList.length; i++) {
        //         if (cuisineList[i].trim() != "") {
        //             if (cuisineList[i] in orlandoCuisines) {
        //                 orlandoCuisines[cuisineList[i]] = orlandoCuisines[cuisineList[i]] + 1;
        //             } else {
        //                 orlandoCuisines[cuisineList[i]] = 1;
        //             }
        //         }
        //     }

        // } else if (item.City == 'Albany') {
        //     var i;
        //     for (i = 0; i < cuisineList.length; i++) {
        //         if (cuisineList[i].trim() != "") {
        //             if (cuisineList[i].trim() in AlabanyCuisines) {
        //                 AlabanyCuisines[cuisineList[i]] = AlabanyCuisines[cuisineList[i]] + 1;
        //             } else {
        //                 AlabanyCuisines[cuisineList[i]] = 1;
        //             }
        //         }
        //     }

        // }


        if (((itemRating >= rating && itemRating < rating + 1) || rating == 0)
            && (cuisineList.indexOf(cuisine) != -1 || cuisine == "0")
        ) { //if location is matched 
            addMarker(item.Longitude, item.Latitude, item.name);
            //   seriesData.push([item.Name, item.rating]);
                  // nodeTemplate = nodeTemplate + 
            // `
            // <div class="col-md-6">
            //     <div class="restaurant card">
            //         <div class="img-container" 
            //             style='background-image: url("/static/images/img2.jpeg");'>
            //             <!--<img src=${item.image}></img>-->
            //         </div>
            //         <div class="data-container">
            //             <div class="card-body-line-1">
            //                 ${item.Name}
            //             </div>
            //             <div class="card-body-line-2">
            //                 <div>${item.Cuisines}</div>
            //                 <span>${item.rating}</span>
            //             </div>
            //             <div class="card-body-line-3">
            //                 ${item.Address}
            //             </div>
            //         </div>
            //     </div>
            // </div>
            // `;

            var cuisinename = item.Cuisines;
            switch(cuisinename){
                case "Indian":
                            nodeTemplate = nodeTemplate +
                                `
                    <div class="col-md-6">
                        <div class="restaurant card">
                            <div class="img-container" 
                                style='background-image: url("/static/images/indian.jpeg");'>
                                <!--<img src=${item.image}></img>-->
                            </div>
                            <div class="data-container">
                                <div class="card-body-line-1">
                                    ${item.name}
                                </div>
                                <div class="card-body-line-2">
                                    <div>${item.Cuisines}</div>
                                    <span>${item.rating}</span>
                                </div>
                                <div class="card-body-line-3">
                                    ${item.Address}
                                    <span>${item.City}</span>
                                        <span>${item.State}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    `;
                    break;
                case "American":
                    nodeTemplate = nodeTemplate +
                                    `
                        <div class="col-md-6">
                            <div class="restaurant card">
                                <div class="img-container" 
                                    style='background-image: url("/static/images/american.jpg");'>
                                    <!--<img src=${item.image}></img>-->
                                </div>
                                <div class="data-container">
                                    <div class="card-body-line-1">
                                        ${item.name}
                                    </div>
                                    <div class="card-body-line-2">
                                        <div>${item.Cuisines}</div>
                                        <span>${item.rating}</span>
                                    </div>
                                    <div class="card-body-line-3">
                                        ${item.Address}
                                        <span>${item.City}</span>
                                        <span>${item.State}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        `;
                        break;
                case "Caribbean":
                                nodeTemplate = nodeTemplate +
                                `
                    <div class="col-md-6">
                        <div class="restaurant card">
                            <div class="img-container" 
                                style='background-image: url("/static/images/caribbean.jpg");'>
                                <!--<img src=${item.image}></img>-->
                            </div>
                            <div class="data-container">
                                <div class="card-body-line-1">
                                    ${item.name}
                                </div>
                                <div class="card-body-line-2">
                                    <div>${item.Cuisines}</div>
                                    <span>${item.rating}</span>
                                </div>
                                <div class="card-body-line-3">
                                    ${item.Address}
                                    <span>${item.City}</span>
                                        <span>${item.State}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                    `;
                    break;
                case "Chinese":
                    nodeTemplate = nodeTemplate +
                                    `
                        <div class="col-md-6">
                            <div class="restaurant card">
                                <div class="img-container" 
                                    style='background-image: url("/static/images/chinese.jpg");'>
                                    <!--<img src=${item.image}></img>-->
                                </div>
                                <div class="data-container">
                                    <div class="card-body-line-1">
                                        ${item.name}
                                    </div>
                                    <div class="card-body-line-2">
                                        <div>${item.Cuisines}</div>
                                        <span>${item.rating}</span>
                                    </div>
                                    <div class="card-body-line-3">
                                        ${item.Address}
                                        <span>${item.City}</span>
                                        <span>${item.State}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        `;
                        break;
                case "Canadian":
                    nodeTemplate = nodeTemplate +
                                    `
                        <div class="col-md-6">
                            <div class="restaurant card">
                                <div class="img-container" 
                                    style='background-image: url("/static/images/canadian.jpg");'>
                                    <!--<img src=${item.image}></img>-->
                                </div>
                                <div class="data-container">
                                    <div class="card-body-line-1">
                                        ${item.name}
                                    </div>
                                    <div class="card-body-line-2">
                                        <div>${item.Cuisines}</div>
                                        <span>${item.rating}</span>
                                    </div>
                                    <div class="card-body-line-3">
                                        ${item.Address}
                                        <span>${item.City}</span>
                                        <span>${item.State}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        `;
                        break;
                case "Italian":
                    nodeTemplate = nodeTemplate +
                                    `
                        <div class="col-md-6">
                            <div class="restaurant card">
                                <div class="img-container" 
                                    style='background-image: url("/static/images/italian.jpg");'>
                                    <!--<img src=${item.image}></img>-->
                                </div>
                                <div class="data-container">
                                    <div class="card-body-line-1">
                                        ${item.name}
                                    </div>
                                    <div class="card-body-line-2">
                                        <div>${item.Cuisines}</div>
                                        <span>${item.rating}</span>
                                    </div>
                                    <div class="card-body-line-3">
                                        ${item.Address}
                                        <span>${item.City}</span>
                                        <span>${item.State}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        `;
                        break;
                case "Japanese":
                    nodeTemplate = nodeTemplate +
                                    `
                        <div class="col-md-6">
                            <div class="restaurant card">
                                <div class="img-container" 
                                    style='background-image: url("/static/images/japanese.jpg");'>
                                    <!--<img src=${item.image}></img>-->
                                </div>
                                <div class="data-container">
                                    <div class="card-body-line-1">
                                        ${item.name}
                                    </div>
                                    <div class="card-body-line-2">
                                        <div>${item.Cuisines}</div>
                                        <span>${item.rating}</span>
                                    </div>
                                    <div class="card-body-line-3">
                                        ${item.Address}
                                        <span>${item.City}</span>
                                        <span>${item.State}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        `;
                        break;
                case "Malaysian":
                    nodeTemplate = nodeTemplate +
                                    `
                        <div class="col-md-6">
                            <div class="restaurant card">
                                <div class="img-container" 
                                    style='background-image: url("/static/images/malaysian.jpg");'>
                                    <!--<img src=${item.image}></img>-->
                                </div>
                                <div class="data-container">
                                    <div class="card-body-line-1">
                                        ${item.name}
                                    </div>
                                    <div class="card-body-line-2">
                                        <div>${item.Cuisines}</div>
                                        <span>${item.rating}</span>
                                    </div>
                                    <div class="card-body-line-3">
                                        ${item.Address}
                                        <span>${item.City}</span>
                                        <span>${item.State}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        `;
                        break;
                case "Mediterranean":
                    nodeTemplate = nodeTemplate +
                                    `
                        <div class="col-md-6">
                            <div class="restaurant card">
                                <div class="img-container" 
                                    style='background-image: url("/static/images/mediterranean.jpg");'>
                                    <!--<img src=${item.image}></img>-->
                                </div>
                                <div class="data-container">
                                    <div class="card-body-line-1">
                                        ${item.name}
                                    </div>
                                    <div class="card-body-line-2">
                                        <div>${item.Cuisines}</div>
                                        <span>${item.rating}</span>
                                    </div>
                                    <div class="card-body-line-3">
                                        ${item.Address}
                                        <span>${item.City}</span>
                                        <span>${item.State}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        `;
                        break;
                case "Mexican":
                    nodeTemplate = nodeTemplate +
                                    `
                        <div class="col-md-6">
                            <div class="restaurant card">
                                <div class="img-container" 
                                    style='background-image: url("/static/images/mexican.jpg");'>
                                    <!--<img src=${item.image}></img>-->
                                </div>
                                <div class="data-container">
                                    <div class="card-body-line-1">
                                        ${item.name}
                                    </div>
                                    <div class="card-body-line-2">
                                        <div>${item.Cuisines}</div>
                                        <span>${item.rating}</span>
                                    </div>

                                    <div class="card-body-line-3">
                                        ${item.Address}
                                        <span>${item.City}</span>
                                        <span>${item.State}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        `;
                        break;
                case "Portuguese":
                    nodeTemplate = nodeTemplate +
                                    `
                        <div class="col-md-6">
                            <div class="restaurant card">
                                <div class="img-container" 
                                    style='background-image: url("/static/images/portuguese.jpg");'>
                                    <!--<img src=${item.image}></img>-->
                                </div>
                                <div class="data-container">
                                    <div class="card-body-line-1">
                                        ${item.name}
                                    </div>
                                    <div class="card-body-line-2">
                                        <div>${item.Cuisines}</div>
                                        <span>${item.rating}</span>
                                    </div>
                                    <div class="card-body-line-3">
                                        ${item.Address}
                                        <span>${item.City}</span>
                                        <span>${item.State}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        `;
                        break;
                case "Thai":
                    nodeTemplate = nodeTemplate +
                                    `
                        <div class="col-md-6">
                            <div class="restaurant card">
                                <div class="img-container" 
                                    style='background-image: url("/static/images/thai.jpg");'>
                                    <!--<img src=${item.image}></img>-->
                                </div>
                                <div class="data-container">
                                    <div class="card-body-line-1">
                                        ${item.name}
                                    </div>
                                    <div class="card-body-line-2">
                                        <div>${item.Cuisines}</div>
                                        <span>${item.rating}</span>
                                    </div>
                                    <div class="card-body-line-3">
                                        ${item.Address}
                                        <span>${item.City}</span>
                                        <span>${item.State}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        `;
                        break;
                case "Vegetarian":
                    nodeTemplate = nodeTemplate +
                                    `
                        <div class="col-md-6">
                            <div class="restaurant card">
                                <div class="img-container" 
                                    style='background-image: url("/static/images/vegetarian.jpg");'>
                                    <!--<img src=${item.image}></img>-->
                                </div>
                                <div class="data-container">
                                    <div class="card-body-line-1">
                                        ${item.name}
                                    </div>
                                    <div class="card-body-line-2">
                                        <div>${item.Cuisines}</div>
                                        <span>${item.rating}</span>
                                    </div>
                                    <div class="card-body-line-3">
                                        ${item.Address}
                                        <span>${item.City}</span>
                                        <span>${item.State}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        `;
                        break;
                default:
                    nodeTemplate = nodeTemplate +
                                    `
                        <div class="col-md-6">
                            <div class="restaurant card">
                                <div class="img-container" 
                                    style='background-image: url("/static/images/american.jpg");'>
                                    <!--<img src=${item.image}></img>-->
                                </div>
                                <div class="data-container">
                                    <div class="card-body-line-1">
                                        ${item.name}
                                    </div>
                                    <div class="card-body-line-2">
                                        <div>${item.Cuisines}</div>
                                        <span>${item.rating}</span>
                                    </div>
                                    <div class="card-body-line-3">
                                        ${item.Address}
                                        <span>${item.City}</span>
                                        <span>${item.State}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                        `;
            }   
        }
    });

    // for (var key in avgCostOrlando){
    //     seriesAvgCostOrlando.push([key, avgCostOrlando[key]]);
    // }
    // for (var key in avgCostAlbany){
    //     seriesAvgCostAlbany.push([key, avgCostAlbany[key]]);
    // }
    // const sortFn = (a, b) => a[0].key - b[0].key;
    // seriesAvgCostOrlando.sort(sortFn);
    // seriesAvgCostAlbany.sort(sortFn);

    // let arr = [5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100];
    // arr = arr.filter(key => key in avgCostOrlando || key in avgCostAlbany);
    // seriesAvgCostOrlando = arr.map(key => {
    //     if (key in avgCostOrlando) {
    //         return [key, avgCostOrlando[key]];
    //     } else {
    //         return [key, 0];
    //     }
    // })
    // seriesAvgCostAlbany = arr.map(key => {
    //     if (key in avgCostAlbany) {
    //         return [key, avgCostAlbany[key]];
    //     } else {
    //         return [key, 0];
    //     }
    // })

    // for (var key in orlandoCuisines) {
    //     seriesCuisiniesOrlando.push([key, orlandoCuisines[key]]);
    // }
    // for (var key in AlabanyCuisines) {
    //     seriesCuisiniesAlabany.push([key, AlabanyCuisines[key]]);
    // }

    // for (var key in orlandoRatings) {
    //     seriesRatingsOrlando.push([key, orlandoRatings[key]]);
    // }
    // for (var key in AlabanyRatings) {
    //     seriesRatingsAlbany.push([key, AlabanyRatings[key]]);
    // }

    node.insertAdjacentHTML('beforeend', nodeTemplate);
    // makeGraphs(seriesData); //call to highcharts
    // makeGraphs();
    // makeGraph2();
    // makeGraph4();
    // makeGraph3();
}

function goHome() {

    fetch("/logout", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(r => r.json())
        .then(response => {
            if (response.message === "Success") {
                sessionStorage.clear();
                window.location = "/";

            }
            else {
                window.alert("Unable to Logout!");
            }
            console.log('Success:', JSON.stringify(response));
        }).catch(error => {
            console.error('Error:', error)
        });
}

//intiate map
function initMap() {

    map = new google.maps.Map(document.getElementById('map'), {
        zoom: 10,
        center: new google.maps.LatLng(36.1699, -115.1398),
        mapTypeId: google.maps.MapTypeId.ROADMAP,
        scrollwheel: false
    });

    // Create a <script> tag and set the USGS URL as the source.
    var script = document.createElement('script');

    script.src = onLoadHandler();
    document.getElementsByTagName('head')[0].appendChild(script);
}

// Adds a marker to the map and push to the array.
function addMarker(longitude, latitude, restaurantName) {
    // var rs = location.split(",");
    // console.log(parseFloat(rs[0]));
    // var myLatLng = {lat: parseFloat(rs[0]), lng: parseFloat(rs[1])};
    var myLatLng = { lat: latitude, lng: longitude };
    var marker = new google.maps.Marker({
        position: myLatLng,
        // animation:google.maps.Animation.BOUNCE,
        map: map
    });
    markers.push(marker);

    //restaurants name on pop-up info window
    var infowindow = new google.maps.InfoWindow({
        content: restaurantName

    });

    // If you want to open info window on click
    // google.maps.event.addListener(marker, 'click', function() {
    //     infowindow.open(map, this);
    // });

    google.maps.event.addListener(marker, 'mouseover', function() {
        infowindow.setContent(restaurantName);
        infowindow.open(map, this);
    });

    google.maps.event.addListener(marker, 'mouseout', function() {
        infowindow.close();
    });
    //infowindow.open(map, marker);
}

// Sets the map on all markers in the array.
function setMapOnAll(map) {
    for (var i = 0; i < markers.length; i++) {
        markers[i].setMap(map);
    }
}

// Removes the markers from the map, but keeps them in the array.
function clearMarkers() {
    setMapOnAll(null);
}

// Shows any markers currently in the array.
function showMarkers() {
    setMapOnAll(map);
}

// Deletes all markers in the array by removing references to them.
function deleteMarkers() {
    clearMarkers();
    markers = [];
}


// Loop through the results array and place a marker for each
// set of coordinates.
window.eqfeed_callback = function (results) {
    for (var i = 0; i < results.features.length; i++) {
        var coords = results.features[i].geometry.coordinates;
        var latLng = new google.maps.LatLng(coords[1], coords[0]);
        var marker = new google.maps.Marker({
            position: latLng,
            map: map
        });
    }
}

//  $(document).ready(function () {
// function makeGraphs() {
//     var chart = Highcharts.chart({
//         chart: {
//             renderTo: 'container',
//             type: 'column'
//         },

//         title: {
//             text: 'Restaurants Rating Comparison'
//         },

//         legend: {
//             align: 'right',
//             verticalAlign: 'middle',
//             layout: 'vertical'
//         },

//         rangeSelector: {
//             selected: 4
//         },

//         xAxis: {
//             minPadding: 0.05,
//             maxPadding: 0.05,
//             type: 'category',
//             title: {
//                 text: 'Restaurants'
//             },
//             labels: {
//                 x: -10
//             }
//         },
//         yAxis: {
//             allowDecimals: false,
//             title: {
//                 text: 'Rating'
//             }
//         },
//         labels: {
//             formatter: function () {
//                 return (this.value);
//             }
//         },
//         plotOptions: {
//             series: {
//                 dataLabels: {
//                     enabled: true,
//                     format: '{point.label}'
//                 },
//                 tooltip: {
//                     formatter: function () {
//                         return '<b>' + '</b><br/>' +
//                             + this.point.y;
//                     }
//                 }
//             },
//             spline: {
//                 marker: {
//                     enabled: true
//                 }
//             }
//         },

//         series: [{
//             name: 'Orlando',
//             data: seriesDataOrlando
//         }, {
//             name: 'Albany',
//             data: seriesDataAlbany
//         }],
//         responsive: {
//             rules: [{
//                 condition: {
//                     maxWidth: 500
//                 },
//                 chartOptions: {
//                     legend: {
//                         align: 'center',
//                         verticalAlign: 'bottom',
//                         layout: 'horizontal'
//                     },
//                     yAxis: {
//                         labels: {
//                             align: 'left',
//                             x: 0,
//                             y: -5
//                         },
//                         title: {
//                             text: null
//                         }
//                     },
//                     subtitle: {
//                         text: null
//                     },
//                     credits: {
//                         enabled: false
//                     }
//                 }
//             }]
//         }
//     });
// };

// function makeGraph2() {
//     var chart = Highcharts.chart({
//         chart: {
//             renderTo: 'container2',
//             type: 'line'
//         },

//         title: {
//             text: 'Restaurant Count - Average cost for Two'
//         },

//         legend: {
//             align: 'right',
//             verticalAlign: 'middle',
//             layout: 'vertical'
//         },

//         rangeSelector: {
//             selected: 4
//         },

//         xAxis: {
//             minPadding: 0.05,
//             maxPadding: 0.05,
//             type: 'series',
//             //allowDecimals: false,
//             title: {
//                 text: 'Average Cost for Two People                      '
//             },
//             labels: {
//                 x: -10
//             }
//         },
//         yAxis: {
//             allowDecimals: false,
//             title: {
//                 text: 'Restaurant Count'
//             }
//         },
//         labels: {
//             formatter: function () {
//                 return (this.value);
//             }
//         },
//         plotOptions: {
//             series: {
//                 dataLabels: {
//                     enabled: true,
//                     format: '{point.label}'
//                 },
//                 tooltip: {
//                     formatter: function () {
//                         return '<b>' + '</b><br/>' +
//                             + this.point.y;
//                     }
//                 }
//             },
//             spline: {
//                 marker: {
//                     enabled: true
//                 }
//             }
//         },

//         series: [{
//             name: 'Orlando',
//             data: seriesAvgCostOrlando
//         }, {
//             name: 'Albany',
//             data: seriesAvgCostAlbany
//         }],
//         responsive: {
//             rules: [{
//                 condition: {
//                     maxWidth: 500
//                 },
//                 chartOptions: {
//                     legend: {
//                         align: 'center',
//                         verticalAlign: 'bottom',
//                         layout: 'horizontal'
//                     },
//                     yAxis: {
//                         labels: {
//                             align: 'left',
//                             x: 0,
//                             y: -5
//                         },
//                         title: {
//                             text: null
//                         }
//                     },
//                     subtitle: {
//                         text: null
//                     },
//                     credits: {
//                         enabled: false
//                     }
//                 }
//             }]
//         }
//     });
// };



// function makeGraph4() {
//     var chart = Highcharts.chart({
//         chart: {
//             renderTo: 'container4',
//             type: 'column'
//         },

//         title: {
//             text: 'Restaurants Cuisine Count'
//         },

//         legend: {
//             align: 'right',
//             verticalAlign: 'middle',
//             layout: 'vertical'
//         },

//         rangeSelector: {
//             selected: 4
//         },

//         xAxis: {
//             minPadding: 0.05,
//             maxPadding: 0.05,
//             type: 'category',
//             title: {
//                 text: 'Cuisines'
//             },
//             labels: {
//                 x: -10
//             }
//         },
//         yAxis: {
//             allowDecimals: false,
//             title: {
//                 text: 'Restaurant Count'
//             }
//         },
//         labels: {
//             formatter: function () {
//                 return (this.value);
//             }
//         },
//         plotOptions: {
//             series: {
//                 dataLabels: {
//                     enabled: true,
//                     format: '{point.label}'
//                 },
//                 tooltip: {
//                     formatter: function () {
//                         return '<b>' + '</b><br/>' +
//                             + this.point.y;
//                     }
//                 }
//             },
//             spline: {
//                 marker: {
//                     enabled: true
//                 }
//             }
//         },

//         series: [{
//             name: 'Orlando',
//             data: seriesCuisiniesOrlando
//         }, {
//             name: 'Albany',
//             data: seriesCuisiniesAlabany
//         }],
//         responsive: {
//             rules: [{
//                 condition: {
//                     maxWidth: 500
//                 },
//                 chartOptions: {
//                     legend: {
//                         align: 'center',
//                         verticalAlign: 'bottom',
//                         layout: 'horizontal'
//                     },
//                     yAxis: {
//                         labels: {
//                             align: 'left',
//                             x: 0,
//                             y: -5
//                         },
//                         title: {
//                             text: null
//                         }
//                     },
//                     subtitle: {
//                         text: null
//                     },
//                     credits: {
//                         enabled: false
//                     }
//                 }
//             }]
//         }
//     });
// };



// function makeGraph3() {
//     var chart = Highcharts.chart({
//         chart: {
//             renderTo: 'container3',
//             type: 'column'
//         },

//         title: {
//             text: 'Restaurants Rating Count'
//         },

//         legend: {
//             align: 'right',
//             verticalAlign: 'middle',
//             layout: 'vertical'
//         },

//         rangeSelector: {
//             selected: 4
//         },

//         xAxis: {
//             minPadding: 0.05,
//             maxPadding: 0.05,
//             type: 'category',
//             title: {
//                 text: 'Ratings'
//             },
//             labels: {
//                 x: -10
//             }
//         },
//         yAxis: {
//             allowDecimals: false,
//             title: {
//                 text: 'Restaurant Count'
//             }
//         },
//         labels: {
//             formatter: function () {
//                 return (this.value);
//             }
//         },
//         plotOptions: {
//             series: {
//                 dataLabels: {
//                     enabled: true,
//                     format: '{point.label}'
//                 },
//                 tooltip: {
//                     formatter: function () {
//                         return '<b>' + '</b><br/>' +
//                             + this.point.y;
//                     }
//                 }
//             },
//             spline: {
//                 marker: {
//                     enabled: true
//                 }
//             }
//         },

//         series: [{
//             name: 'Orlando',
//             data: seriesRatingsOrlando
//         }, {
//             name: 'Albany',
//             data: seriesRatingsAlbany
//         }],
//         responsive: {
//             rules: [{
//                 condition: {
//                     maxWidth: 500
//                 },
//                 chartOptions: {
//                     legend: {
//                         align: 'center',
//                         verticalAlign: 'bottom',
//                         layout: 'horizontal'
//                     },
//                     yAxis: {
//                         labels: {
//                             align: 'left',
//                             x: 0,
//                             y: -5
//                         },
//                         title: {
//                             text: null
//                         }
//                     },
//                     subtitle: {
//                         text: null
//                     },
//                     credits: {
//                         enabled: false
//                     }
//                 }
//             }]
//         }
//    });
// };
