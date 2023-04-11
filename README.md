# Health Tracker - Android App 

<img src="/readme/app_icon.png" align="left"
width="200" hspace="10" vspace="10">

Health Tracker is all in one app that compasses tracking your 
diet and your physical activity. The app allows one to log their 
daily meal and the app will automatically determines the macro/micronutrients 
within the foods. You can also record your workouts and get a good idea on 
how much calories your are burning and have a visual of your gym routine. Finally,
the app provides month analytics considering both your diet and exercise. 

Health Tracker is available on the Google Play Store!

<p align="left">
<a href="https://play.google.com/store/apps/details?id=com.group11.healthtrackerapp">
    <img alt="Get it on Google Play"
        height="80"
        src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" />
</a>
        </p>

## Features

The android app lets you:
- Record your workouts daily and the food you ate
- View your past workouts and food you ate
- Automatically determine micro/macronutrients of meals using nutritionix api
- Get visuals and analytics on your health
- No ads

## Screenshots

[<img src="/readme/ss1.png" align="left"
width="200"
    hspace="10" vspace="10">](/readme/ss1.png)
[<img src="/readme/ss2.png" align="center"
width="200"
    hspace="10" vspace="10">](/readme/ss2.png)
[<img src="/readme/ss3.png" align="right"
width="200"
hspace="10" vspace="10">](/readme/ss3.png)

[<img src="/readme/ss4.png" align="left"
width="200"
hspace="10" vspace="10">](/readme/ss4.png)
[<img src="/readme/ss5.png" align="center"
width="200"
hspace="10" vspace="10">](/readme/ss5.png)
[<img src="/readme/ss6.png" align="right"
width="200"
hspace="10" vspace="10">](/readme/ss6.png)

## Locally Run the App

Run the app on Android Studio using device with API version 30+:

The only addition that is required to run the app is that within the src/main/res/values create file named api_key.xml
and have 2 string resources with name="api_key" and name="api_id". Then go to https://developer.nutritionix.com/ and create and account
and use the the generated api_key and api_id. Here is a screenshot of the return file:

<img src="/readme/api.png"
width="200" hspace="10" vspace="10">

After go to the run tab to start up the app, and start using it!

## Future Features

In the near future we will be adding:
- Add authentication system to prevent data from stored login
- connect application to our backend instead of directly connecting to nutritionix
- add different workout visuals and tutorial on how perform workout


## Contribution:
The app was developed by Vaibhav, Qixin, Shrey, Xinrui, Xianru, Ella
