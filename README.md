# Independent Software Development Project (Orbital):

##  ![App Logo][logo] Application name: SG Spot Sports

[logo]: https://github.com/ajax-yz/SGSpotSports/blob/master/app/src/main/res/mipmap-mdpi/ic_launcher_round.png

### Table of Contents:
1. [Motivation & Target Audience](https://github.com/ajax-yz/SGSpotSports#1-motivation--target-audience-)
2. [User Stories](https://github.com/ajax-yz/SGSpotSports#2-user-stories-)
3. [Wireframe](https://github.com/ajax-yz/SGSpotSports#3-wireframe-)
4. [Features](https://github.com/ajax-yz/SGSpotSports#4-features-)
5. [Feaures yet to be implemented](https://github.com/ajax-yz/SGSpotSports#5-features-yet-to-be-implemented-)
6. [Technology/Dependecy Used](https://github.com/ajax-yz/SGSpotSports#6-technologydependency-used-)
7. [Support](https://github.com/ajax-yz/SGSpotSports#7-support-)

## 1) Motivation & Target Audience 🎯

In today's society, the world is changing rapidly and people tend to lead a hectic lifestyle pursuing greater achievement. In the process, they tend to neglect their physical health due to several reasons such as feeling tired from work, lack of time to exercise, and etc. According to the World Health Organization, the prevalence of obesity across the globe has almost tripled between 1975 and 2016. The increasing trend of obesity is worrying because its underlying impact would be a higher risk of developing obesity-related health problems such as diabetes, heart disease and stroke which would result in higher health cost for every country and reduced productivity. In view of this issue, our team came up with SG Spot Sports, a friendly mobile application designed to facilitate and ease the trouble of finding the desired physical activities to participate and makes exercising a fun activity! The target audience for our application includes anyone who are seeking an active lifestyle. 🏃

## 2) User Stories 📰

1.	As a working adult, I would like to minimize the hassle of locating a nearby sports facility to engage in physical activity. Instead, I would like to spend more quality time exercising and achieving a healthy lifestyle.

2.	As a young adult, I would like to keep an active and healthy lifestyle and achieving it through the sports that interest me would further motivate me to work harder towards my personal goals

3.	As a teenager/young children, sports activity is a great way to improve my physical fitness level and at the same time foster stronger bonds with my family and friends.

4.	As a user, I would like to be able to plan my activities ahead and receive reminders about my activities nearer to the date. Additionally, I would like to receive a simple navigation guide to direct me to the sports facility

5.	As an administrator, I would want to ensure a timely update on the availability of the sports facilities (undergoing renovation, and etc) and with the help of the users, we can ensure that the application would provide accurate details of the facilities available around the country.


## 3) Wireframe 🔗

![Wireframe](https://github.com/ajax-yz/SGSpotSports/blob/master/ReadMe-Images/wireframe.png "Wireframe snapshot")

### Typical User Flow:

| Stages:                          | Actions:                       |
| ----------------------           |:----------------------------: |
| **Signup**                       | Sign up is needed to facilitate the Kaki (Buddy) system, and for users to contribute by registering markers to the map.  |
| **Login**                        | Users will login to manage his network of friends, and list of markers.     |
| **Edit Profile**                 | Users are able to set their profile picture, and change their status whenever they please.      |
| **Manage/Create Markers**        | Under the manage list of markers, users can easily contribute by clicking on the add marker icon located at the bottom right. |
| **Choose Location**              | Upon clicking the add marker, a map will be launched according to the device's location. Thereafter, users can register the sports facility around him to the map.      |
| **Fill Up Details**              | Compulsory details and picture of the sports facility will have to be populated to indicate and describe the type of facility. Picture will be useful for other users to validate the authenticity of the marker     |
| **Browse Nearby Markers**        | Once submitted, the markers would be synchronized and appear on the map for every users on the platform. |
| **Search for Facility**          | There is a handy search function located at the main facility locator tab, for users to quickly search for his/her desired facility      |
| **Click Marker for Info**        | Markers can be clicked to display more information about the facility such as the address and picture.    |
| **Contact friends**             | After finding the desired sports and facility, users can invite their friends by chatting with them through the messaging function |
| **Set Reminder Alert**          | Once decided, users can conveniently set reminder alert through the activity planner tab to notify themselves of the upcoming sports activity.|

## 4) Features 👨‍💻

1. Sports Facility Locator
    - Users can easily view sports facilities around his/her vicinity through markers with icons indicating the type of sports facility.

    - Users may also search for a particular facility through the search function for extra convenience.

    - Clicking on the marker will reveal more detailed information about the facility such as the address and picture of the facility.

    - Through Google Map API, we have also included a place picker function for users to see facilities around them that are already registered under Google Maps.

2. Active Lifestyle Planner
    - Enables the users to plan ahead to engage in a sports activity on a specific date set by the users.

    - Reminders can be set to alert the user before the actual date and time of the activity.

3. Kaki (Buddy) System
    - Users may choose to add each other as friends, to maintain in contact and motivate each other to stay active through regular invitations to engage in sports activities.

    - The Kaki system will enable users to chat with each other and arrange for future matches. This would encourage more social interaction within the society, and enable users to make new friends and meet people of the same passion.


## 5) Features yet to be Implemented 🔧

> Due to timeline constraints and unforeseen errors in the system, certain features could not be fully accomplished yet.

1. Search function
   - Current search function could not search for markers registered by users, instead it only searches for markers officially registered in Google Maps.


2. Registration of markers
   - Tend to be off the coordinates according to testing, and glitches whenever the map is being zoomed in and out


3. Facility information
   - XML layout has been established for facility information to display the facility name, address, picture, description, reviews, and buttons for phone, directions and add to activity planner.

   - However, due to technical errors, mapping of variable details have not been successful. Hence, the feature has yet to be accomplished.

## 6) Technology/Dependency Used 🔨

### Platform:
> [**Android Studio**](https://developer.android.com/studio)


### Google Maps, Places, Routes SDK API documentation:
Link: [**Click here to set up API**](https://developers.google.com/maps/documentation/android-sdk/intro)

### Dependecy:
```JAVA
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:27.1.1'
    implementation 'com.android.support.constraint:constraint-layout:1.1.2'
    implementation 'com.android.support:design:27.1.1'
    implementation "com.android.support:support-compat:27.1.1"
    // Google Maps API
    implementation 'com.google.android.gms:play-services-maps:15.0.1'
    implementation 'com.google.android.gms:play-services-location:15.0.1'
    implementation 'com.google.android.gms:play-services-places:15.0.1'
    // Firebase
    implementation 'com.google.firebase:firebase-core:16.0.1'
    implementation 'com.google.firebase:firebase-firestore:17.1.0'
    implementation 'com.google.firebase:firebase-auth:16.0.3'
    implementation 'com.google.firebase:firebase-storage:16.0.1'
    implementation 'com.google.firebase:firebase-database:16.0.1'
    implementation 'com.google.firebase:firebase-messaging:17.3.0'
    implementation 'com.android.support:multidex:1.0.3'

    // Firebase UI
    implementation 'com.firebaseui:firebase-ui-database:4.1.0'
    implementation "android.arch.core:runtime:1.1.1"
    implementation "android.arch.core:common:1.1.1"

    // Glide (image upload)
    implementation 'com.github.bumptech.glide:glide:4.7.1'
    implementation 'com.android.support:support-v4:27.1.1'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.7.1'

    // Circle Image View
    implementation 'de.hdodenhof:circleimageview:2.2.0'

    // Crop Image
    implementation 'com.theartofdev.edmodo:android-image-cropper:2.7.0'

    // Picasso
    implementation 'com.squareup.picasso:picasso:2.71828'

    // Okhttp library
    implementation 'com.squareup.okhttp3:okhttp:3.11.0'

    // Image Compressor
    implementation 'id.zelory:compressor:2.1.0'

    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
    implementation 'com.android.support:cardview-v7:27.1.1'
}

apply plugin: 'com.google.gms.google-services'
```

## 7) Support 📧

### Contact me for any enquiries:
Email: c.yizhao7@gmail.com
